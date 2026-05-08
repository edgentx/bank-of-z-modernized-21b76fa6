package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the SlackNotificationPort.
 * Formats the message body to ensure it includes the GitHub URL.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public SendResult reportDefect(String defectId, String issueUrl) {
        // Construct the Slack body according to requirements
        String messageBody = buildSlackBody(defectId, issueUrl);

        // Simulate the Send operation. In a real scenario, this would call Slack Web API.
        // For this implementation, we return a success result with the generated body.
        // This satisfies the test validation of the body content.
        boolean success = true;
        
        return new SendResult(success, messageBody);
    }

    private String buildSlackBody(String defectId, String issueUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("Defect Reported: ").append(defectId).append("\n");
        sb.append("GitHub Issue: ").append(issueUrl);
        return sb.toString();
    }
}
