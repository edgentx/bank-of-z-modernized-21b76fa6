package com.example.config;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.workflows.ReportDefectWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Temporal Workflows and dependencies.
 * In a real environment, this would also set up the Temporal Worker and WorkflowClient.
 */
@Configuration
public class TemporalConfiguration {

    /**
     * Bean definition for the ReportDefectWorkflow.
     * Wired with the actual adapter implementations.
     */
    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        return new ReportDefectWorkflow.WorkflowImpl(gitHubPort, slackNotificationPort);
    }
}
