package com.example.application;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service handling the logic for reporting defects.
 * Orchestrates the creation of the message and dispatch via the Slack port.
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the VForce360 channel via Slack.
     * This method ensures the body contains the GitHub URL as per VW-454.
     *
     * @param issueUrl The full URL to the GitHub issue.
     */
    public void reportDefect(String issueUrl) {
        if (issueUrl == null || issueUrl.isBlank()) {
            throw new IllegalArgumentException("Issue URL cannot be blank");
        }
        
        // Construct the message body including the URL as required by VW-454
        String messageBody = "Defect reported: " + issueUrl;
        
        slackNotificationPort.sendNotification(messageBody);
    }
}