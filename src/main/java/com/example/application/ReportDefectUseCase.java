package com.example.application;

import com.example.ports.SlackNotificationPort;

/**
 * Use Case handling the business logic for reporting a defect.
 * Orchestrates the creation of the Slack message.
 */
public class ReportDefectUseCase {

    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectUseCase(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    public void execute(ReportDefectCommand cmd) {
        // Logic to format the message with cmd.githubUrl()
        String urlDisplay = (cmd.githubUrl() != null) ? cmd.githubUrl() : "URL: N/A";
        
        String messageBody = String.format(
            "Defect Reported: %s\nLink: %s",
            cmd.title(),
            urlDisplay 
        );

        slackNotificationPort.send("#vforce360-issues", messageBody);
    }
}