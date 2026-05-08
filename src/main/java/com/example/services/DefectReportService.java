package com.example.services;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating defect reporting.
 * This encapsulates the business logic required to format the message
 * and trigger the notification via the Slack Port.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    // Constructor Injection (Adhering to Spring Boot and Adapter Pattern requirements)
    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect identified by its ID.
     * This method formats the message body to include the GitHub issue URL
     * and delegates the sending to the provided SlackNotificationPort.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @return true if the notification was successfully sent.
     */
    public boolean reportDefect(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null or blank");
        }

        // Logic to construct the body containing the GitHub URL
        // Based on the Defect Description and Expected Behavior
        String githubUrl = "https://github.com/example-bank/z/issues/" + defectId;
        String messageBody = "Defect reported: " + defectId + ". GitHub issue: " + githubUrl;

        return slackNotificationPort.sendNotification(messageBody);
    }
}