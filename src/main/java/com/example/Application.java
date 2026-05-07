package com.example;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.workflow.ReportDefectWorkflow;
import com.example.workflow.ReportDefectWorkflowImpl;
import com.example.adapters.RealGitHubIssueAdapter;
import com.example.adapters.RealSlackNotificationAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Bank of Z Main Application
 * Configures Workflow and Adapters.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GitHubIssuePort gitHubIssuePort() {
        return new RealGitHubIssueAdapter();
    }

    @Bean
    public SlackNotificationPort slackNotificationPort() {
        return new RealSlackNotificationAdapter();
    }

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        return new ReportDefectWorkflowImpl(gitHubPort, slackPort);
    }
}
