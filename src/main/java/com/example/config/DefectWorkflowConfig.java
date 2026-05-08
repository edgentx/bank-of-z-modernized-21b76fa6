package com.example.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for Defect Reporting workflow dependencies.
 * Wires up the real adapters for GitHub and Slack.
 */
@Configuration
public class DefectWorkflowConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GitHubIssuePort gitHubIssuePort(
            RestTemplate restTemplate,
            @Value("${github.api.url:https://api.github.com/repos/bank-of-z/core/issues}") String apiUrl) {
        return new GitHubIssueAdapter(restTemplate, apiUrl);
    }

    @Bean
    public MethodsClient slackMethodsClient(Slack slack) {
        return slack.methods();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort(MethodsClient slackMethodsClient) {
        return new SlackNotificationAdapter(slackMethodsClient);
    }

    @Bean
    public Slack slack(
            @Value("${slack.token:}") String slackToken) {
        // In a real scenario, the token must be set. For the test context,
        // this might be initialized with a dummy token or not used at all if mocks are active.
        // Assuming Spring Boot's test properties or profile will inject a valid test token if needed.
        return Slack.getInstance();
    }
}
