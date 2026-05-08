package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Application Service handling the defect reporting workflow.
 * Orchestrates the domain logic and external notifications.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection of the Port (Adapter Pattern)
    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the reporting of a defect.
     * Generates the domain event, persists it (in memory for this phase), and notifies Slack.
     *
     * @param cmd The command containing defect details.
     * @return The ID of the generated defect.
     */
    public String reportDefect(ReportDefectCmd cmd) {
        String defectId = UUID.randomUUID().toString();
        DefectAggregate aggregate = new DefectAggregate(defectId);

        // Execute domain logic
        var events = aggregate.execute(cmd);

        // In a real app, we would persist events here via EventStorePort.
        // For S-FB-1, we react to the event immediately.
        events.forEach(event -> {
            if (event instanceof com.example.domain.defect.model.DefectReportedEvent reportedEvent) {
                notifySlack(reportedEvent);
            }
        });

        return defectId;
    }

    private void notifySlack(com.example.domain.defect.model.DefectReportedEvent event) {
        // Expected format: "GitHub issue: <url>"
        String messageBody = "Defect Reported for Project: " + event.projectId() + "\n" +
                            "Severity: " + event.severity() + "\n" +
                            "GitHub issue: " + event.githubIssueUrl();

        // Channel determined by requirements/story context
        slackNotificationPort.sendMessage("#vforce360-issues", messageBody);
    }
}
