package org.dcsa.reefer.commercial.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.reefer.commercial.domain.persistence.entity.OutgoingReeferCommercialEvent;
import org.dcsa.reefer.commercial.domain.persistence.repository.OutgoingReeferCommercialEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReeferCommercialEventDeliveryService {
  private final OutgoingReeferCommercialEventRepository outgoingRepository;
  private final TransactionTemplate transactionTemplate;

  @Value("${dcsa.reefer-commercial.delivery.max-threads-per-processor:2}")
  private Integer maxThreadsPerProcessor = 2;

  @Value("${dcsa.reefer-commercial.delivery.max-total-threads:8}")
  private Integer maxTotalThreads = 8;

  private ExecutorService executor = Executors.newCachedThreadPool();

  // @Scheduled(
  //   initialDelayString = "${dcsa.reefer-commercial.delivery.initial-delay:5}",
  //   fixedDelayString = "${dcsa.reefer-commercial.delivery.fixed-delay:10}",
  //   timeUnit = TimeUnit.SECONDS
  // )
  public void deliverScheduled() throws InterruptedException{
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
    eventOpt.ifPresent(event -> {
      log.info("outgoing {}", event);
      // more stuff
    });

    return false; // event.isPresent();
  }
}
