package com.example.config;

import com.example.adapters.GitHubRestAdapter;
import com.example.adapters.SlackWebhookAdapter;
import com.example.domain.validation.ReportDefectCommandHandler;
import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Spring Configuration for the Validation context.
 * Wires the ports and adapters for ReportDefectCommandHandler.
 */
@Configuration
public class ValidationConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public GitHubIssueTracker gitHubIssueTracker(RestClient.Builder builder) {
        return new GitHubRestAdapter(
            "https://api.github.com", // Defaults overridden by @Value in adapter
            "bank-of-z",
            "project",
            null, // Token injected by @Value
            builder
        );
    }

    @Bean
    public SlackNotifier slackNotifier(RestClient.Builder builder) {
        return new SlackWebhookAdapter(
            null, // Webhook URL injected by @Value
            builder
        );
    }

    @Bean
    public ReportDefectCommandHandler reportDefectCommandHandler(
            GitHubIssueTracker gitHubIssueTracker,
            SlackNotifier slackNotifier) {
        return new ReportDefectCommandHandler(gitHubIssueTracker, slackNotifier);
    }
}
