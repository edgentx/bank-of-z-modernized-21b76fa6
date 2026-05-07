package com.example.domain.verification.service;

import org.springframework.stereotype.Service;
import com.example.ports.SlackNotificationPort;

/**
 * Service handling verification workflows.
 * VW-454: Ensures Slack notifications include relevant GitHub issue links.
 */
@Service
public class VerificationService {

    private final SlackNotificationPort slackNotificationPort;

    public VerificationService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect or verification issue to the VForce360 channel.
     * @param defectTitle The title of the defect.
     * @param defectUrl The GitHub URL associated with this defect/issue.
     */
    public void reportDefect(String defectTitle, String defectUrl) {
        // Validate inputs to ensure the URL is present
        if (defectUrl == null || defectUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub Issue URL cannot be blank when reporting defect.");
        }
        if (defectTitle == null || defectTitle.isBlank()) {
            throw new IllegalArgumentException("Defect Title cannot be blank.");
        }

        // Format the message body
        String messageBody = String.format("VForce360 Alert: %s%nGitHub Issue: <%s>", defectTitle, defectUrl);

        // Send via the port
        slackNotificationPort.notifyChannel(messageBody);
    }
}
