package org.dcsa.reefer.commercial.service;

import org.dcsa.reefer.commercial.domain.persistence.entity.ReeferCommercialEventSubscription;
import org.dcsa.reefer.commercial.domain.persistence.repository.ReeferCommercialEventSubscriptionRepository;
import org.dcsa.reefer.commercial.service.mapping.ReeferCommercialEventSubscriptionMapper;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionCreateRequestTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateRequestTO;
import org.dcsa.reefer.commercial.transferobjects.ReeferCommercialEventSubscriptionUpdateSecretRequestTO;
import org.dcsa.skernel.errors.exceptions.BadRequestException;
import org.dcsa.skernel.errors.exceptions.NotFoundException;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReeferCommercialEventSubscriptionServiceTest {
  @Mock
  private ReeferCommercialEventSubscriptionRepository repository;

  @Spy
  private ReeferCommercialEventSubscriptionMapper mapper = Mappers.getMapper(ReeferCommercialEventSubscriptionMapper.class);

  @InjectMocks
  private ReeferCommercialEventSubscriptionService service;

  @Test
  public void testFindSubscriptions() {
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    when(repository.findAll(any(Pageable.class)))
      .thenReturn(new PageImpl(List.of(reeferCommercialEventSubscription(id, now))));

    PagedResult<ReeferCommercialEventSubscriptionTO> actual =
      service.findSubscriptions(PageRequest.of(0, 10));

    assertEquals(1, actual.content().size());
    assertEquals(reeferCommercialEventSubscriptionTO(id, now), actual.content().get(0));
  }

  @Test
  public void testFindSubscription() {
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    when(repository.findById(any(UUID.class))).thenReturn(Optional.of(reeferCommercialEventSubscription(id, now)));
    ReeferCommercialEventSubscriptionTO actual = service.findSubscription(id);
    assertEquals(reeferCommercialEventSubscriptionTO(id, now), actual);
  }

  @Test
  public void testFindSubscription_NotFound() {
    UUID id = UUID.randomUUID();
    when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());
    NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findSubscription(id));
    assertEquals("No reefer-commercial-event-subscriptions found for subscriptionID = " + id, exception.getMessage());
  }

  @Test
  public void testCreateSubscription() {
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    when(repository.save(any(ReeferCommercialEventSubscription.class))).thenAnswer(answer -> {
      ReeferCommercialEventSubscription s = answer.getArgument(0);
      return s.toBuilder()
        .id(id)
        .createdDateTime(now)
        .updatedDateTime(now)
        .build();
    });

    ReeferCommercialEventSubscriptionTO actual = service.createSubscription(
      ReeferCommercialEventSubscriptionCreateRequestTO.builder()
        .equipmentReference("MSKU9070323")
        .carrierBookingReference("ABC1234567")
        .callbackUrl("https://callback.url/")
        .build());

    assertEquals(reeferCommercialEventSubscriptionTO(id, now), actual);
    verify(repository).save(any(ReeferCommercialEventSubscription.class));
  }

  @Test
  public void testUpdateSubscription() {
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    when(repository.findById(any(UUID.class))).thenReturn(Optional.of(reeferCommercialEventSubscription(id, now)));

    when(repository.save(any(ReeferCommercialEventSubscription.class))).thenAnswer(answer -> {
      ReeferCommercialEventSubscription s = answer.getArgument(0);
      return s.toBuilder()
        .createdDateTime(now)
        .updatedDateTime(now)
        .build();
    });

    ReeferCommercialEventSubscriptionTO actual = service.updateSubscription(id,
      ReeferCommercialEventSubscriptionUpdateRequestTO.builder()
        .id(id)
        .equipmentReference("APZU4812090")
        .carrierBookingReference("CBA1234567")
        .callbackUrl("https://another.callback.url/")
        .build());

    assertEquals(reeferCommercialEventSubscriptionTO2(id, now), actual);
    verify(repository).findById(id);
    verify(repository).save(any(ReeferCommercialEventSubscription.class));
  }

  @Test
  public void testUpdateSubscription_NotFound() {
    UUID id = UUID.randomUUID();

    when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
      () -> service.updateSubscription(id, ReeferCommercialEventSubscriptionUpdateRequestTO.builder().id(id).build())
    );

    assertEquals("No reefer-commercial-event-subscriptions found for subscriptionID = " + id, exception.getMessage());

    verify(repository).findById(id);
    verify(repository, never()).save(any(ReeferCommercialEventSubscription.class));
  }

  @Test
  public void testUpdateSubscription_BadRequest() {
    UUID id = UUID.randomUUID();

    BadRequestException exception = assertThrows(BadRequestException.class,
      () -> service.updateSubscription(id, ReeferCommercialEventSubscriptionUpdateRequestTO.builder().id(UUID.randomUUID()).build())
    );

    assertEquals("subscriptionIDs must match", exception.getMessage());

    verify(repository, never()).findById(id);
    verify(repository, never()).save(any(ReeferCommercialEventSubscription.class));
  }

  @Test
  public void testUpdateSubscriptionSecret() {
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    when(repository.findById(any(UUID.class))).thenReturn(Optional.of(reeferCommercialEventSubscription(id, now)));
    when(repository.save(any(ReeferCommercialEventSubscription.class))).thenAnswer(answer -> answer.getArgument(0));

    service.updateSubscriptionSecret(id, new ReeferCommercialEventSubscriptionUpdateSecretRequestTO(new byte[10]));

    verify(repository).findById(id);
    verify(repository).save(any(ReeferCommercialEventSubscription.class));
  }

  @Test
  public void testUpdateSubscriptionSecret_NotFound() {
    UUID id = UUID.randomUUID();

    when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
      () -> service.updateSubscriptionSecret(id, new ReeferCommercialEventSubscriptionUpdateSecretRequestTO(new byte[10]))
    );

    assertEquals("No reefer-commercial-event-subscriptions found for subscriptionID = " + id, exception.getMessage());

    verify(repository).findById(id);
    verify(repository, never()).save(any(ReeferCommercialEventSubscription.class));
  }

  @Test
  public void testDeleteSubscription() {
    UUID id = UUID.randomUUID();

    service.deleteSubscription(id);

    verify(repository).deleteById(id);
  }

  private ReeferCommercialEventSubscription reeferCommercialEventSubscription(UUID id, OffsetDateTime now) {
    return ReeferCommercialEventSubscription.builder()
      .id(id)
      .equipmentReference("MSKU9070323")
      .carrierBookingReference("ABC1234567")
      .callbackUrl("https://callback.url/")
      .createdDateTime(now)
      .updatedDateTime(now)
      .build();
  }

  private ReeferCommercialEventSubscriptionTO reeferCommercialEventSubscriptionTO(UUID id, OffsetDateTime now) {
    return ReeferCommercialEventSubscriptionTO.builder()
      .id(id)
      .equipmentReference("MSKU9070323")
      .carrierBookingReference("ABC1234567")
      .callbackUrl("https://callback.url/")
      .createdDateTime(now)
      .updatedDateTime(now)
      .build();
  }

  private ReeferCommercialEventSubscriptionTO reeferCommercialEventSubscriptionTO2(UUID id, OffsetDateTime now) {
    return ReeferCommercialEventSubscriptionTO.builder()
      .id(id)
      .equipmentReference("APZU4812090")
      .carrierBookingReference("CBA1234567")
      .callbackUrl("https://another.callback.url/")
      .createdDateTime(now)
      .updatedDateTime(now)
      .build();
  }
}
