package com.example.config;

import com.example.adapters.WebClientSlackAdapter;
import com.example.ports.SlackPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class SpringConfig {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Value("${slack.timeout:5000}")
    private int slackTimeout;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(slackTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(slackTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(slackTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    @Bean
    public SlackPort slackPort(OkHttpClient okHttpClient, ObjectMapper mapper) {
        return new WebClientSlackAdapter(slackWebhookUrl, okHttpClient, mapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
