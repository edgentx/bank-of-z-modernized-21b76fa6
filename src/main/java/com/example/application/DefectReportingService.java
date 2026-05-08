package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the workflow of reporting a defect.
 * Orchestrates the Aggregate logic and triggers the notification via the port.
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Entry point for the Temporal Activity or REST controller.
     * Executes the command to report the defect and notifies Slack if successful.
     */
    public void reportDefect(String defectId, String title, String description, String githubUrl, String channel) {
        var aggregate = new DefectAggregate(defectId);
        var cmd = new ReportDefectCmd(defectId, title, description, githubUrl, channel);

        // Execute domain logic
        var events = aggregate.execute(cmd);

        // Handle side effects (Notification)
        for (var event : events) {
            if (event instanceof DefectReportedEvent e) {
                sendNotification(e);
            }
        }
    }

    private void sendNotification(DefectReportedEvent event) {
        // Format the message body according to specification
        // "Slack body includes GitHub issue: <url>"
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Reported: ").append(event.title()).append("\n");
        bodyBuilder.append("Description: ").append(event.description().isBlank() ? "N/A" : event.description()).append("\n");
        // S-FB-1 Fix: Ensure the GitHub URL is explicitly included
        bodyBuilder.append("GitHub issue: ").append(event.githubUrl());

        slackNotificationPort.postMessage(event.channel(), bodyBuilder.toString());
    }
}
