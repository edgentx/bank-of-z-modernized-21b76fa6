package com.example.config;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.slack.enabled", havingValue = "true", matchIfMissing = false)
public class SlackConfig {

    @Bean
    public Slack slack() {
        return Slack.getInstance();
    }

    @Bean
    public MethodsClient methodsClient(Slack slack, @Value("${app.slack.token}") String token) {
        return slack.methods(token);
    }
}