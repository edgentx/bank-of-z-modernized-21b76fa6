package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service responsible for reporting defects to external notification channels.
 * This is the System Under Test (SUT) for S-FB-1.
 */
@Service
public class DefectReporter {

    private static final Logger logger = LoggerFactory.getLogger(DefectReporter.class);
    private static final String SLACK_CHANNEL = "#vforce360-issues";
    private static final String GITHUB_BASE_URL = "https://github.com";

    private final SlackNotificationPort slackPort;

    /**
     * Constructor for dependency injection.
     * In production, Spring injects the real adapter.
     * In tests, the mock port is injected.
     */
    public DefectReporter(SlackNotificationPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect to the configured Slack channel.
     * Generates a formatted message including a simulated GitHub URL.
     *
     * @param id The defect ID (e.g., "VW-454")
     * @param title The title of the defect
     * @param details Additional key-value details about the defect
     */
    public void reportDefect(String id, String title, Map<String, Object> details) {
        logger.info("Reporting defect {}: {}", id, title);

        // Construct the GitHub issue URL.
        // Note: We construct a generic valid GitHub URL structure.
        String issueUrl = GITHUB_BASE_URL + "/" + id + "/" + sanitizeTitle(title);

        // Build the Slack message body.
        // Using <url|optional text> format is standard for Slack unfurling, but <url> is valid.
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("*Defect Reported: ").append(id).append("*\n");
        bodyBuilder.append("> ").append(title).append("\n");
        
        if (details != null && !details.isEmpty()) {
            bodyBuilder.append("```\n");
            details.forEach((k, v) -> bodyBuilder.append(k).append(": ").append(v).append("\n"));
            bodyBuilder.append("```\n");
        }

        bodyBuilder.append("GitHub Issue: <").append(issueUrl).append(">\n");

        String messageBody = bodyBuilder.toString();

        logger.debug("Posting message to {}: {}", SLACK_CHANNEL, messageBody);
        slackPort.postMessage(SLACK_CHANNEL, messageBody);
    }

    /**
     * Sanitizes the title to be URL-safe (simple implementation for testing).
     */
    private String sanitizeTitle(String title) {
        if (title == null) return "issue";
        return title.toLowerCase().replaceAll("[^a-z0-9]", "-");
    }
}
