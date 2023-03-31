Event Delivery System
============================================================================================

This component contains a reusable event delivery system

## Usage

To use it you will need to implement a few things yourself

### Tables

The event delivery system uses 3 tables that must be defined:

```sql
CREATE TABLE outgoing_event_message (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL,
    subscription_id uuid NOT NULL,
    next_delivery_attempt timestamp with time zone NOT NULL default now(),
    delivery_attempts int NOT NULL default 0
);
CREATE INDEX ON outgoing_event_message (next_delivery_attempt);
CREATE UNIQUE INDEX ON outgoing_event_message (event_id, subscription_id);

CREATE TABLE delivered_event_message (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL,
    subscription_id uuid NOT NULL,
    callback_url text NOT NULL,
    delivery_time timestamp with time zone NOT NULL,
    delivery_attempts int NOT NULL
);

CREATE TABLE undeliverable_event_message (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_id varchar(100) NOT NULL,
    subscription_id uuid NOT NULL,
    callback_url text NULL,
    last_delivery_attempt timestamp with time zone NOT NULL,
    delivery_attempts int NOT NULL,
    error_details text NOT NULL
);
```

Optionally the ```event_id``` fields can reference your event table and if you do not wish to delete subscriptions the ```subscriptions_id```
fields can reference your subscriptions table.

### RestTemplate

You must ensure that your application context contains a RestTemplate, for example make a configuration class like this:

```java
@Configuration
public class RestTemplateConfiguration {

  @Value("${dcsa.rest.connect-timeout:10}")
  private Integer connectTimeout;

  @Value("${dcsa.rest.read-timeout:10}")
  private Integer readTimeout;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
      .setConnectTimeout(Duration.ofSeconds(connectTimeout))
      .setReadTimeout(Duration.ofSeconds(readTimeout))
      .build();
  }
}
```

### Implement EventDeliveryHelperService

You will also need to implement a helper service named ```EventDeliveryHelperService``` that contains two methods, one
for retrieving the subscription details and one for retrieving the event in the format it should be sent in.

For example:

```java
@Service
@AllArgsConstructor
public class EventDeliveryHelperServiceImpl implements EventDeliveryHelperService {
  private final ReeferCommercialEventRepository reeferCommercialEventRepository;
  private final ReeferCommercialEventSubscriptionRepository subscriptionRepository;
  private final ReeferCommercialEventMapper reeferCommercialEventMapper;

  @Override
  @Transactional
  public Optional<EventSubscription> findSubscriptionById(UUID subscriptionId) {
    return subscriptionRepository.findById(subscriptionId)
      .map(s -> EventSubscription.builder()
        .subscriptionId(s.getId())
        .callbackUrl(s.getCallbackUrl())
        .secret(s.getSecret())
        .build());
  }

  @Override
  @Transactional
  public Optional<Object> findEventByIdAsTO(String eventId) {
    return reeferCommercialEventRepository.findById(eventId)
      .map(ReeferCommercialEvent::getContent)
      .map(reeferCommercialEventMapper::toDTO);
  }
}
```

Note these two methods are not allowed to return ```null```, if no subscription or event is found they must return ```Optional.empty()```.

### Populate outgoing_event_message table

```OutgoingEventMessage``` and ```OutgoingEventMessageRepository``` exists to easily populate the outgoing_event_message table with
the events you wish to send.

Example:

```java
  outgoingEventMessageRepository.save(OutgoingEventMessage.of(subscriptionId, eventId));

```

Messages are automatically sent and the two status tables ```delivered_event_message``` and ```undeliverable_event_message```
are populated with the result.

### Configuration

The following shows the default configurable values for the event delivery system:

```yaml
dcsa:
  specification:
    version: N/A
  event-delivery:
    initial-delay: 5
    fixed-delay: 10
    backoff-delays: "1, 1, 60, 1, 1, 120, 1, 1, 360, 1, 1, 720, 1, 1, 1440, 1, 1"
    max-threads-per-processor: 2
    max-total-threads: 8
```

It is recommended to at least set ```dcsa.specification.version``` to a reasonable value as this is used in the ```API-Version``` http header
when sending messages, all other values are optional.

A note about threads: The lowest value of ```max-threads-per-processor``` times processor on the system and ```max-total-threads```
is used as the maximum number of threads to deliver messages. So using the above defaults on an 8-core system a maximum of 8 threads
are used since ```max-total-threads``` is lowest and on a dual-core system a max of 4 threads will be used since thread-per-processor becomes
lower than max-threads in that case. If configuring the system to use more threads ensure you have enough resources (memory, db-connections,
etc.) to run that amount of threads.

