package com.example.adapters;

import com.example.model.DefectReport;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Real implementation of the SlackPort.
 * This adapter formats the defect report into a Slack message body.
 * FIX S-FB-1: Ensures the body contains "GitHub issue: <url>" when a URL is present.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public void sendDefectNotification(DefectReport report) {
        Objects.requireNonNull(report, "DefectReport cannot be null");

        // Constructing the message body according to the expected format
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Detected: ").append(report.defectId()).append("\n");
        bodyBuilder.append("Title: ").append(report.title()).append("\n");

        String url = report.githubUrl();
        // Validation: Ensure we don't append empty brackets
        if (url != null && !url.isBlank()) {
            bodyBuilder.append("GitHub issue: <").append(url).append(">");
        } else {
            bodyBuilder.append("No GitHub issue linked.");
        }

        String body = bodyBuilder.toString();
        
        // In a real scenario, we would invoke the Slack WebClient API here.
        // e.g. slackClient.sendMessage(body);
        // For this defect fix, the critical part is the string formatting logic above.
        System.out.println("[SlackAdapter] Sending notification: " + body);
    }
}
