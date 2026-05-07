package com.example;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Main Application entry point and definitions.
 * S-FB-1: Wiring for ReportDefect Workflow.
 */
public class Application {

    // Spring Boot main method would be here if not already present.
    // public static void main(String[] args) { SpringApplication.run(Application.class, args); }

    /**
     * Temporal Workflow Orchestrator stub.
     * This component is responsible for coordinating the defect reporting process:
     * 1. Create GitHub Issue
     * 2. Notify Slack with the result
     */
    @Component
    public static class ReportDefectWorkflowOrchestrator {
        private final GitHubPort gitHub;
        private final SlackNotificationPort slack;

        // Spring will inject the mock ports during tests if configured,
        // or real adapters during runtime.
        public ReportDefectWorkflowOrchestrator(GitHubPort gitHub, SlackNotificationPort slack) {
            this.gitHub = gitHub;
            this.slack = slack;
        }

        public void execute(String channel, String title, String description) {
            // Implementation for S-FB-1
            // 1. Create the GitHub issue
            String issueUrl = gitHub.createIssue(title, description);

            // 2. Notify Slack with the specific format required
            // Format: "Slack body includes GitHub issue: <url>"
            String slackBody = "GitHub issue: " + issueUrl;
            slack.sendText(channel, slackBody);
        }
    }
}
