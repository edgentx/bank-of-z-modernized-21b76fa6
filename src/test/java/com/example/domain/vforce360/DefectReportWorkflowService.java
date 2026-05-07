package com.example.domain.vforce360;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service wrapper for the Temporal Workflow/Activity logic.
 * Orchestrates the reporting of a defect to GitHub and notification to Slack.
 */
public class DefectReportWorkflowService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflowService.class);
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;
    private final ObjectMapper objectMapper;

    public DefectReportWorkflowService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Executes the defect report workflow:
     * 1. Create GitHub Issue
     * 2. Notify Slack with Issue URL in the body
     *
     * @param title Defect title
     * @param description Defect description
     */
    public void reportDefect(String title, String description) {
        log.info("Executing defect report workflow for: {}", title);

        // Step 1: Create the issue in GitHub
        String issueUrl = gitHubPort.createIssue(title, description);
        log.info("GitHub issue created: {}", issueUrl);

        // Step 2: Construct Slack payload including the returned URL
        String slackPayload = formatSlackMessage(title, issueUrl);

        // Step 3: Send the notification
        boolean sent = slackPort.send(slackPayload);
        if (!sent) {
            log.error("Failed to send Slack notification for issue: {}", issueUrl);
            // Depending on requirements, we might throw an exception here.
            // For now, we log the failure.
        }
    }

    /**
     * Formats a Slack message payload including the GitHub URL.
     * This ensures the regression test finds the URL in the body.
     */
    private String formatSlackMessage(String title, String url) {
        try {
            ObjectNode message = objectMapper.createObjectNode();
            // Using a simple text field for compatibility with the mock adapter checks
            // Real Slack adapters might use 'blocks', but 'text' is standard for simple messages
            message.put("text", String.format("Defect Reported: %s\nGitHub Issue: %s", title, url));
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("Failed to format Slack message", e);
            // Fallback to a simple string if JSON fails
            return String.format("Defect Reported: %s\nGitHub Issue: %s", title, url);
        }
    }
}
