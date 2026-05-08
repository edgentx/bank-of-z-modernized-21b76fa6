package com.example.config;

import com.example.adapters.RealGitHubAdapter;
import com.example.adapters.RealSlackAdapter;
import com.example.domain.validation.model.ReportDefectWorkflow;
import com.example.domain.validation.model.ReportDefectWorkflowImpl;
import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for the Validation domain.
 * Wires ports and adapters.
 */
@Configuration
public class ValidationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(name = "slackNotifier")
    public SlackNotifier realSlackAdapter(RestTemplate restTemplate) {
        // In a real app, pull URL from properties
        return new RealSlackAdapter("https://hooks.slack.com/services/T00/B00/XXX", restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "gitHubIssueTracker")
    public GitHubIssueTracker realGitHubAdapter(RestTemplate restTemplate) {
        // In a real app, pull URL from properties
        return new RealGitHubAdapter("https://api.github.com/repos/example/repo", restTemplate);
    }

    // Bean for the Workflow implementation.
    // Note: Temporal Spring Boot Starter usually handles this via @WorkflowImpl scanning,
    // but we expose it here explicitly for non-Temporal usage or testing if needed.
    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(SlackNotifier slack, GitHubIssueTracker gitHub) {
        return new ReportDefectWorkflowImpl(slack, gitHub);
    }
}
