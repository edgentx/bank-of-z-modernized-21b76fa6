package com.example.domain.defect;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Workflow implementation for reporting defects.
 * This class represents the SUT (System Under Test) for the feature.
 * In a real Temporal scenario, this would be the Workflow implementation.
 */
public class ReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflow.class);

    private final SlackNotificationPort slackPort;
    private final GitHubIssuePort githubPort;

    public ReportDefectWorkflow(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    /**
     * Coordinates the defect reporting process:
     * 1. Create GitHub Issue
     * 2. Notify Slack with results (including URL if available)
     * 
     * @param title The defect title
     * @param repo The repository name
     */
    public void reportDefect(String title, String repo) {
        log.info("Reporting defect: {} on repo {}", title, repo);

        Optional<String> githubUrl = githubPort.createIssue(repo, title);

        String slackBody;
        if (githubUrl.isPresent() && !githubUrl.get().isBlank()) {
            slackBody = "Defect Reported. GitHub Issue: " + githubUrl.get();
        } else {
            slackBody = "Defect Reported. Failed to create GitHub issue.";
        }

        slackPort.postDefect(title, slackBody);
    }
}
