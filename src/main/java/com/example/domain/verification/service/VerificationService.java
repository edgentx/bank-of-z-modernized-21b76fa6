package com.example.domain.verification.service;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.verification.model.ReportDefectCommand;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Service handling verification logic, including defect reporting workflows.
 * Story S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
public class VerificationService extends AggregateRoot {

    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);
    private static final String SLACK_CHANNEL_ID = "vforce360-issues"; // Default channel from defect description

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    // Constructor for Dependency Injection (typically used by @Service or Workflow)
    public VerificationService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        super();
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    // No-args constructor for testing/simple instantiation if needed, though ID is usually required for AggregateRoot
    public VerificationService() {
        super();
        this.gitHubPort = null;
        this.slackPort = null;
    }

    /**
     * Handles the ReportDefectCommand (S-FB-1).
     * 1. Creates a GitHub Issue.
     * 2. Posts a notification to Slack containing the GitHub Issue URL.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCommand cmd) {
        if (gitHubPort == null || slackPort == null) {
            throw new IllegalStateException("Dependencies not initialized");
        }

        log.info("Processing defect report: {}", cmd.defectId());

        // Step 1: Create Issue on GitHub
        // Note: The interface requires (title, body, labels). We map the command to these params.
        Map<String, String> labels = new HashMap<>();
        labels.put("severity", cmd.severity());
        labels.put("project", "S-FB-1");

        String githubUrl = gitHubPort.createIssue(cmd.title(), cmd.description(), labels);

        // Step 2: Compose Slack Body
        // AC: Slack body includes GitHub issue: <url>
        String slackBody = String.format(
                "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
                cmd.title(),
                cmd.severity(),
                githubUrl
        );

        // Step 3: Post to Slack
        slackPort.postMessage(SLACK_CHANNEL_ID, slackBody);

        log.info("Defect {} processed. Issue created at {} and notification sent to {}.",
                cmd.defectId(), githubUrl, SLACK_CHANNEL_ID);
    }

    @Override
    public String id() {
        // This service acts as a stateless worker in this context, returning a fixed ID
        return "verification-service";
    }
}
