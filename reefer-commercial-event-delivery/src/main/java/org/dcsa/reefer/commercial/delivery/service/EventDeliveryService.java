package org.dcsa.reefer.commercial.delivery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.delivery.persistence.entity.DeliveredEventMessage;
import org.dcsa.reefer.commercial.delivery.persistence.entity.OutgoingEventMessage;
import org.dcsa.reefer.commercial.delivery.persistence.entity.UndeliverableEventMessage;
import org.dcsa.reefer.commercial.delivery.persistence.repository.DeliveredEventMessageRepository;
import org.dcsa.reefer.commercial.delivery.persistence.repository.OutgoingEventMessageRepository;
import org.dcsa.reefer.commercial.delivery.persistence.repository.UndeliverableEventMessageRepository;
import org.dcsa.reefer.commercial.delivery.service.exception.EventDeliveryException;
import org.dcsa.reefer.commercial.delivery.service.exception.UnrecoverableEventDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDeliveryService {
  private final EventDeliveryHelperService helperService;
  private final OutgoingEventMessageRepository outgoingRepository;
  private final DeliveredEventMessageRepository deliveredRepository;
  private final UndeliverableEventMessageRepository deadRepository;

  private final TransactionTemplate transactionTemplate;
  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate;

  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Value("${dcsa.event-delivery.max-threads-per-processor:2}")
  private Integer maxThreadsPerProcessor = 2;

  @Value("${dcsa.event-delivery.max-total-threads:8}")
  private Integer maxTotalThreads = 8;

  @Value("${dcsa.specification.version:N/A}")
  private String apiVersion = "N/A";

  @Value("${dcsa.event-delivery.backoff-delays:1,1,60,1,1,120,1,1,360,1,1,720,1,1,1440,1,1}")
  private String backoffDelaysString = "1,1,60,1,1,120,1,1,360,1,1,720,1,1,1440,1,1";
  private Integer[] backoffDelays;

  private final AtomicBoolean keepRunning = new AtomicBoolean(true);
  private final AtomicInteger activeTasks = new AtomicInteger(0);

  @PostConstruct
  public void initialize() {
    backoffDelays = Arrays.stream(backoffDelaysString.split(","))
      .map(String::trim)
      .map(Integer::parseInt)
      .toArray(Integer[]::new);
  }

  @Scheduled(
    initialDelayString = "${dcsa.event-delivery.initial-delay:5}",
    fixedDelayString = "${dcsa.event-delivery.fixed-delay:10}",
    timeUnit = TimeUnit.SECONDS
  )
  public void deliverScheduled() {
    int maxThreads = Math.max(2, Math.min(maxTotalThreads, Runtime.getRuntime().availableProcessors() * maxThreadsPerProcessor));
    int currentTasks = activeTasks.get();
    if (keepRunning.get() && currentTasks < maxThreads) {
      long eligible = outgoingRepository.countEligible();
      int additionalTasksNeeded = (int) Math.min(maxThreads, eligible) - currentTasks;
      if (additionalTasksNeeded > 0) {
        log.trace("Spawning {} tasks ({}/{}) to handle {} outgoing messages", additionalTasksNeeded, currentTasks, maxThreads, eligible);
        activeTasks.addAndGet(additionalTasksNeeded);
        for (int i = 0; i < additionalTasksNeeded; i++) {
          executor.submit(this::deliverEvents);
        }
      }
    }
  }

  /**
   * For testing purposes.
   */
  public void deliverBlocking() {
    activeTasks.incrementAndGet();
    deliverEvents();
  }

  @EventListener(ContextClosedEvent.class)
  public void beforeShutdown() {
    keepRunning.set(false);
    executor.shutdown();
  }

  private void deliverEvents() {
    try {
      // Continue until all has been processed
      while (keepRunning.get() && Boolean.TRUE.equals(transactionTemplate.execute(this::deliverEvent)));
    } finally {
      if (activeTasks.decrementAndGet() == 0) {
        log.trace("Last task exited");
      }
    }
  }

  private boolean deliverEvent(TransactionStatus transactionStatus) {
    Optional<OutgoingEventMessage> eventOpt = outgoingRepository.findNext();
    eventOpt.ifPresent(outEvent -> {
      EventSubscription subscription = null;
      try {
        subscription = helperService.findSubscriptionById(outEvent.getSubscriptionId())
            .orElseThrow(() -> new UnrecoverableEventDeliveryException("Unknown or deleted subscription " + outEvent.getSubscriptionId()));

        Object event = helperService.findEventByIdAsTO(outEvent.getEventId())
            .orElseThrow(() -> new UnrecoverableEventDeliveryException("Unknown event " + outEvent.getEventId()));

        log.debug("Outgoing event '{}' to '{}={}' - {} previous attempts", outEvent.getEventId(), subscription.subscriptionId(), subscription.callbackUrl(), outEvent.getDeliveryAttempts());

        Response response = restTemplate.execute(subscription.callbackUrl(), HttpMethod.POST, addPostContent(subscription, event), this::extractResponse);
        if (!response.status().is2xxSuccessful()) {
          throw new EventDeliveryException("Callback returned " + response.status + ": " + response.content());
        }

        log.debug("Delivered event '{}' to '{}={}'", outEvent.getEventId(), subscription.subscriptionId(), subscription.callbackUrl());

        deliveredRepository.save(DeliveredEventMessage.builder()
          .id(outEvent.getId())
          .eventId(outEvent.getEventId())
          .subscriptionId(subscription.subscriptionId())
          .callbackUrl(subscription.callbackUrl())
          .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
          .deliveryTime(OffsetDateTime.now())
          .build());
        outgoingRepository.delete(outEvent);
      } catch (UnrecoverableEventDeliveryException e) {
        if (subscription != null) {
          log.warn("Unable to deliver event '{}' to '{}={}' after {} attempts: {}", outEvent.getEventId(), subscription.subscriptionId(), subscription.callbackUrl(), outEvent.getDeliveryAttempts() + 1, e.getMessage(), e);
        } else {
          log.warn("Unable to deliver event '{}' to {} after {} attempts: {}", outEvent.getEventId(), outEvent.getSubscriptionId(), outEvent.getDeliveryAttempts() + 1, e.getMessage(), e);
        }
        deadRepository.save(UndeliverableEventMessage.builder()
          .id(outEvent.getId())
          .eventId(outEvent.getEventId())
          .subscriptionId(outEvent.getSubscriptionId())
          .callbackUrl(subscription != null ? subscription.callbackUrl() : null)
          .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
          .lastDeliveryAttemptTime(OffsetDateTime.now())
          .errorDetails(e.getMessage())
          .build());
        outgoingRepository.delete(outEvent);
      } catch (Exception e) {
        if (outEvent.getDeliveryAttempts() >= backoffDelays.length) {
          log.warn("Unable to deliver event '{}' to '{}={}' after {} attempts: {}", outEvent.getEventId(), subscription.subscriptionId(), subscription.callbackUrl(), outEvent.getDeliveryAttempts() + 1, e.getMessage(), e);
          deadRepository.save(UndeliverableEventMessage.builder()
            .id(outEvent.getId())
            .eventId(outEvent.getEventId())
            .subscriptionId(outEvent.getSubscriptionId())
            .callbackUrl(subscription.callbackUrl())
            .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
            .lastDeliveryAttemptTime(OffsetDateTime.now())
            .errorDetails(e.getMessage())
            .build());
          outgoingRepository.delete(outEvent);
        } else {
          int backoff = backoffDelays[outEvent.getDeliveryAttempts()];
          log.warn("Error delivering event '{}' to '{}={}', attempting again after {} minutes: {}", outEvent.getEventId(), subscription.subscriptionId(), subscription.callbackUrl(), backoff, e.getMessage());
          outgoingRepository.save(outEvent.toBuilder()
            .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
            .nextDeliveryAttemptTime(OffsetDateTime.now().plusMinutes(backoff))
            .build());
        }
      }
    });

    return eventOpt.isPresent();
  }

  private RequestCallback addPostContent(final EventSubscription subscription, final Object event) {
    return (ClientHttpRequest request) -> {
      byte[] body = objectMapper.writeValueAsBytes(List.of(event));
      byte[] signature = computeSha256Signature(subscription.secret(), body);
      request.getHeaders().add("Subscription-ID", subscription.subscriptionId().toString());
      request.getHeaders().add("Notification-Signature", "sha256=" + new String(Hex.encode(signature)));
      request.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
      request.getHeaders().add("API-Version", apiVersion);
      request.getBody().write(body);
    };
  }

  public byte[] computeSha256Signature(byte[] key, byte[] payload) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(key, "HmacSHA256"));
      return mac.doFinal(payload);
    } catch (InvalidKeyException|NoSuchAlgorithmException e) {
      throw new UnrecoverableEventDeliveryException(e.getMessage());
    }
  }

  private record Response(HttpStatusCode status, String content) { }

  private Response extractResponse(ClientHttpResponse response) throws IOException {
    try (Scanner scanner = new Scanner(response.getBody(), StandardCharsets.UTF_8)) {
      scanner.useDelimiter("\\A");
      return new Response(response.getStatusCode(), scanner.hasNext() ? scanner.next() : "");
    }
  }
}
