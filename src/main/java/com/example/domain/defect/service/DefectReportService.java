package com.example.domain.defect.service;

import com.example.domain.shared.DomainEvent;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service responsible for handling defect reporting logic.
 * Constructs the message payload and interacts with the Slack port.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Generates the Slack body for a defect report.
     * This is the implementation logic required to pass VW-454.
     *
     * @param title     The defect title.
     * @param githubUrl The direct link to the GitHub issue.
     * @return A formatted string ready for Slack.
     */
    public String generateSlackBody(String title, String githubUrl) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Defect title cannot be empty");
        }
        // The test strictly checks for the presence of the URL.
        // We format it clearly for the channel.
        if (githubUrl != null && !githubUrl.isBlank()) {
            return String.format("Defect Reported: %s%nGitHub Issue: %s", title, githubUrl);
        }
        return "Defect Reported: " + title;
    }

    /**
     * Orchestrates the reporting of a defect.
     * Simulates the workflow step.
     */
    public void reportDefect(String channel, String title, String githubUrl) {
        String body = generateSlackBody(title, githubUrl);
        slackNotificationPort.sendMessage(channel, body);
    }
}
