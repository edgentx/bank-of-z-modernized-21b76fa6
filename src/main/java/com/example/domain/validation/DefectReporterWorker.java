package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for reporting defects to external systems like Slack.
 * This represents the 'Activity' or logic executed by the Temporal Worker.
 * 
 * Fix for VW-454: Ensures the GitHub URL is included in the notification body.
 */
public class DefectReporterWorker {

    private static final String DEFAULT_CHANNEL = "#vforce360-issues";
    private final SlackNotificationPort slackPort;

    /**
     * Constructor using dependency injection.
     * @param slackPort The port adapter for Slack notifications.
     */
    public DefectReporterWorker(SlackNotificationPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect to the configured Slack channel.
     * Includes the GitHub URL in the message body.
     * 
     * @param githubUrl The URL of the GitHub issue.
     * @throws IllegalArgumentException if githubUrl is null or empty.
     */
    public void reportDefect(String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub URL cannot be null or empty");
        }

        // FIX for VW-454: Construct the body explicitly containing the URL
        String messageBody = String.format(
                "Defect reported. Issue: %s",
                githubUrl
        );

        slackPort.postMessage(DEFAULT_CHANNEL, messageBody);
    }
}
