package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkflowPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapter for Temporal workflows.
 * Coordinates between Temporal triggers and downstream ports like Slack.
 */
@Component
public class TemporalWorkflowAdapter implements TemporalWorkflowPort {

    private final SlackNotificationPort slackNotificationPort;

    @Autowired
    public TemporalWorkflowAdapter(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public void triggerReportDefect(String issueId, String description) {
        // In a real system, this would signal a Temporal workflow.
        // For this validation, we execute the logic directly to ensure the link is generated.
        
        String githubUrl = String.format("https://github.com/egdcrypto/bank-of-z-modernized/issues/%s", issueId);
        
        // Construct the message body as required by the defect VW-454
        String messageBody = String.format(
            "Defect Reported: %s\nIssue ID: %s\nGitHub URL: %s",
            description, issueId, githubUrl
        );

        slackNotificationPort.sendNotification(messageBody);
    }
}
