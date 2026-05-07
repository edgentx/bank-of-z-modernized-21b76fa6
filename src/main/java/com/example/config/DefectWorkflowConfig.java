package com.example.config;

import com.example.defect.ReportDefectWorkflow;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Reporting components.
 * Instantiates the workflow with available port implementations.
 */
@Configuration
public class DefectWorkflowConfig {

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        return new ReportDefectWorkflow(gitHubPort, slackNotificationPort);
    }
}