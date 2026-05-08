package com.example.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for wiring up real adapters vs mocks.
 * In production, these beans are activated by properties.
 * In tests, mocks are injected manually by the test suite.
 */
@Configuration
public class AdapterConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .build();
    }

    @Bean
    @ConditionalOnMissingBean(GitHubIssuePort.class)
    public GitHubIssuePort gitHubIssuePort(OkHttpClient client) {
        // In a real app, these URLs come from @Value("${github.api-url}")
        return new GitHubIssueAdapter(
            client, 
            "https://api.github.com/repos/bank-of-z/issues", 
            "dummy-token"
        );
    }

    @Bean
    @ConditionalOnMissingBean(SlackNotificationPort.class)
    public SlackNotificationPort slackNotificationPort(OkHttpClient client) {
        return new SlackNotificationAdapter(
            client, 
            "https://hooks.slack.com/services/T00/B00/XXXX"
        );
    }
}
