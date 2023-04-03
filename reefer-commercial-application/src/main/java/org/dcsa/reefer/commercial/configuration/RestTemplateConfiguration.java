package org.dcsa.reefer.commercial.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

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
