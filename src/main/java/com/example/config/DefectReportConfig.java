package com.example.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import java.time.Duration;

@Configuration
public class DefectReportConfig {

    @Bean
    @Primary
    public RestClient slackRestClient() {
        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                    .withConnectTimeout(Duration.ofSeconds(5))
                    .withReadTimeout(Duration.ofSeconds(10))))
                .build();
    }
}
