package com.example.application;

import com.example.ports.SlackNotificationPort;

/**
 * Use Case handling the business logic for reporting a defect.
 * Orchestrates the creation of the Slack message.
 * Implementation placeholder for TDD Red Phase.
 */
public class ReportDefectUseCase {

    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectUseCase(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    public void execute(ReportDefectCommand cmd) {
        // TODO: Implement logic to format the message with cmd.githubUrl()
        // This placeholder simply sends the raw title to ensure compilation,
        // causing the specific URL test to fail (Red Phase).
        String urlDisplay = (cmd.githubUrl() != null) ? cmd.githubUrl() : "URL: N/A";
        
        String messageBody = String.format(
            "Defect Reported: %s\nLink: %s",
            cmd.title(),
            urlDisplay 
        );

        slackNotificationPort.send("#vforce360-issues", messageBody);
    }
}
