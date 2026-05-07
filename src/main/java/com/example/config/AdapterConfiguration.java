package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AdapterConfiguration {

    // Note: Instances of GitHubAdapter and SlackNotificationAdapter are created
    // via their constructors annotated with @Autowired by Spring.
    // However, if we need to expose specific beans explicitly (e.g. for @Profile usage),
    // we do it here.

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
