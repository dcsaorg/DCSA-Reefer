package org.dcsa.reefer.commercial.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfiguration {

  @Value("${dcsa.reefer-commercial.delivery.connect-timeout:10}")
  private Integer connectTimeout;

  @Value("${dcsa.reefer-commercial.delivery.read-timeout:10}")
  private Integer readTimeout;

  @Bean("eventDeliveryRestTemplate")
  public RestTemplate eventDeliveryRestTemplate(RestTemplateBuilder builder) {
    return builder
      .setConnectTimeout(Duration.ofSeconds(connectTimeout))
      .setReadTimeout(Duration.ofSeconds(readTimeout))
      .build();
  }
}
