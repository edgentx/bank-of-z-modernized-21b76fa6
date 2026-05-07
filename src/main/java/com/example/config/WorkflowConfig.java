package com.example.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.adapters.TemporalWorkflowAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkflowPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for wiring the Report Defect workflow.
 * This acts as the "Orchestrator" glue code, connecting the Temporal trigger
 * to the actual GitHub and Slack adapters.
 */
@Configuration
public class WorkflowConfig {

    private static final Logger log = LoggerFactory.getLogger(WorkflowConfig.class);

    /**
     * Configures the handler logic for the Report Defect workflow.
     * This bean is injected into the Temporal adapter (or mock) to define
     * what happens when the workflow is triggered.
     */
    @Bean
    @ConditionalOnProperty(name = "app.mode", havingValue = "worker", matchIfMissing = false)
    public TemporalWorkflowPort.ReportDefectHandler reportDefectHandler(
            GitHubIssuePort gitHubPort,
            SlackNotificationPort slackPort) {
        
        return (defectId, summary, description) -> {
            log.info("Executing ReportDefectHandler for {}", defectId);

            // 1. Create GitHub Issue
            String issueUrl = gitHubPort.createIssue(defectId, summary, description);

            // 2. Send Notification to Slack
            slackPort.sendNotification(defectId, summary, issueUrl);

            return issueUrl;
        };
    }
}
