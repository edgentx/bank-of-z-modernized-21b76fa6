package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for REST clients used by adapters.
 */
@Configuration
public class RestConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
