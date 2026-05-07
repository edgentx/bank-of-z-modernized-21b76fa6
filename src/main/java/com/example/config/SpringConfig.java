package com.example.config;

import com.example.adapters.WebClientSlackAdapter;
import com.example.ports.SlackNotifier;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SpringConfig {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public SlackNotifier slackNotifier(OkHttpClient okHttpClient) {
        return new WebClientSlackAdapter(slackWebhookUrl, okHttpClient);
    }
}
