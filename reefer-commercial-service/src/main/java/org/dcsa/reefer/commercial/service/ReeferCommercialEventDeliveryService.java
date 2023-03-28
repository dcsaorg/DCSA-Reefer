package org.dcsa.reefer.commercial.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.domain.persistence.entity.DeliveredReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.entity.OutgoingReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription;
import org.dcsa.reefer.commercial.domain.persistence.entity.UndeliverableReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.repository.DeliveredReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.OutgoingReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventSubscriptionRepository;
import org.dcsa.reefer.commercial.domain.persistence.repository.UndeliverableReeferCommercialEventRepository;
import org.dcsa.reefer.commercial.domain.valueobjects.ReeferCommercialEvent;
import org.dcsa.reefer.commercial.service.exception.EventDeliveryException;
import org.dcsa.reefer.commercial.service.exception.UnrecoverableEventDeliveryException;
import org.dcsa.reefer.commercial.service.mapping.ReeferCommercialEventMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReeferCommercialEventDeliveryService {
  private final OutgoingReeferCommercialEventRepository outgoingRepository;
  private final ReeferCommercialEventSubscriptionRepository subscriptionRepository;
  private final DeliveredReeferCommercialEventRepository deliveredRepository;
  private final UndeliverableReeferCommercialEventRepository deadRepository;
  private final ReeferCommercialEventRepository eventRepository;

  private final ReeferCommercialEventMapper reeferCommercialEventMapper;

  private final TransactionTemplate transactionTemplate;
  private final ObjectMapper objectMapper;

  @Qualifier("eventDeliveryRestTemplate")
  private final RestTemplate restTemplate;

  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Value("${dcsa.reefer-commercial.delivery.max-threads-per-processor:2}")
  private Integer maxThreadsPerProcessor = 2;

  @Value("${dcsa.reefer-commercial.delivery.max-total-threads:8}")
  private Integer maxTotalThreads = 8;

  @Value("${dcsa.specification.version}")
  private String apiVersion = "N/A";

  @Value("${dcsa.reefer-commercial.delivery.max-delivery-attempts:10}")
  private Integer maxDeliveryAttempts = 10;

  @Value("${dcsa.reefer-commercial.delivery.backoff-initial-delay:120}")
  private Integer backoffInitialDelay = 120;

  @Value("${dcsa.reefer-commercial.delivery.backoff-multiplier:3}")
  private Integer backoffMultiplier = 3;

  @Scheduled(
    initialDelayString = "${dcsa.reefer-commercial.delivery.initial-delay:7}",
    fixedDelayString = "${dcsa.reefer-commercial.delivery.fixed-delay:10}",
    timeUnit = TimeUnit.SECONDS
  )
  public void deliverScheduled() throws InterruptedException {
    long eligible = outgoingRepository.countEligible();
    if (eligible > 0) {
      long threads = Math.min(eligible, Math.max(2, Math.min(maxTotalThreads, Runtime.getRuntime().availableProcessors() * maxThreadsPerProcessor)));
      log.debug("Using {} threads to handle {} outgoing events", threads, eligible);
      executor.invokeAll(LongStream.range(0, threads).mapToObj(i -> (Callable<Boolean>) this::deliverEvents).toList());
    }
  }

  private boolean deliverEvents() {
    // Continue until all has been processed
    while (Boolean.TRUE.equals(transactionTemplate.execute(this::deliverEvent)));
    return true;
  }

  private boolean deliverEvent(TransactionStatus transactionStatus) {
    Optional<OutgoingReeferCommercialEvent> eventOpt = outgoingRepository.findNext();
    eventOpt.ifPresent(outEvent -> {
      log.info("outgoing {}", outEvent);
      ReeferCommercialEventSubscription subscription = null;
      ReeferCommercialEvent event = null;
      try {
        subscription =
          subscriptionRepository.findById(outEvent.getSubscriptionId())
            .orElseThrow(() -> new UnrecoverableEventDeliveryException("Unknown or deleted subscription " + outEvent.getSubscriptionId()));

        event =
          eventRepository.findById(outEvent.getEventId())
            .map(org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEvent::getContent)
            .orElseThrow(() -> new UnrecoverableEventDeliveryException("Unknown event " + outEvent.getEventId()));

        Response response = restTemplate.execute(subscription.getCallbackUrl(), HttpMethod.POST, addPostContent(subscription, event), this::extractResponse);
        if (!response.status().is2xxSuccessful()) {
          throw new EventDeliveryException("Callback returned " + response.status + ": " + response.content());
        }

        deliveredRepository.save(DeliveredReeferCommercialEvent.builder()
          .id(outEvent.getId())
          .eventId(event.getEventID())
          .subscriptionId(subscription.getId())
          .callbackUrl(subscription.getCallbackUrl())
          .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
          .deliveryTime(OffsetDateTime.now())
          .build());
        outgoingRepository.delete(outEvent);
      } catch (UnrecoverableEventDeliveryException e) {
        deadRepository.save(UndeliverableReeferCommercialEvent.builder()
          .id(outEvent.getId())
          .eventId(outEvent.getEventId())
          .subscriptionId(outEvent.getSubscriptionId())
          .callbackUrl(subscription != null ? subscription.getCallbackUrl() : null)
          .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
          .lastDeliveryAttemptTime(OffsetDateTime.now())
          .errorDetails(e.getMessage())
          .build());
        outgoingRepository.delete(outEvent);
      } catch (Exception e) {
        if (outEvent.getDeliveryAttempts() > 10) {
          deadRepository.save(UndeliverableReeferCommercialEvent.builder()
            .id(outEvent.getId())
            .eventId(outEvent.getEventId())
            .subscriptionId(outEvent.getSubscriptionId())
            .callbackUrl(subscription != null ? subscription.getCallbackUrl() : null)
            .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
            .lastDeliveryAttemptTime(OffsetDateTime.now())
            .errorDetails(e.getMessage())
            .build());
          outgoingRepository.delete(outEvent);
        } else {
          int backoff = backoffInitialDelay * (int) Math.pow(backoffMultiplier, outEvent.getDeliveryAttempts());
          log.info("Delaying delivery of {} by {} minutes: {}", outEvent, backoff, e.getMessage(), e);
          outgoingRepository.save(outEvent.toBuilder()
            .deliveryAttempts(outEvent.getDeliveryAttempts() + 1)
            .nextDeliveryAttemptTime(OffsetDateTime.now().plusMinutes(backoff))
            .build());
        }
      }
    });

    return eventOpt.isPresent();
  }

  private RequestCallback addPostContent(final ReeferCommercialEventSubscription subscription, final ReeferCommercialEvent event) {
    return (ClientHttpRequest request) -> {
      byte[] body = objectMapper.writeValueAsBytes(List.of(reeferCommercialEventMapper.toDTO(event)));
      byte[] signature = computeSignature(subscription.getSecret(), body);
      request.getHeaders().add("Subscription-ID", subscription.getId().toString());
      request.getHeaders().add("Notification-Signature", "sha256=" + new String(Hex.encode(signature)));
      request.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
      request.getHeaders().add("API-Version", apiVersion);
      request.getBody().write(body);
    };
  }

  public byte[] computeSignature(byte[] key, byte[] payload) {
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
