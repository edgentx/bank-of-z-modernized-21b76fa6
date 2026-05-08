package com.example.adapters;

import com.example.domain.reporting.model.DefectReportedEvent;
import com.example.ports.NotificationFormatterPort;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation for formatting defect notifications into a Slack-compatible format.
 * This addresses the VW-454 defect by ensuring the GitHub URL is explicitly included in the message body.
 */
@Component
public class SlackNotificationFormatterAdapter implements NotificationFormatterPort {

    @Override
    public String formatDefectForSlack(DefectReportedEvent event) {
        // Extract metadata safely
        String githubUrl = "(No URL provided)";
        String severity = "LOW";
        
        if (event.metadata() != null) {
            Object urlObj = event.metadata().get("githubUrl");
            if (urlObj != null) {
                githubUrl = urlObj.toString();
            }
            Object sevObj = event.metadata().get("severity");
            if (sevObj != null) {
                severity = sevObj.toString();
            }
        }

        // Construct the Slack body.
        // CRITICAL FIX for VW-454: Ensure githubUrl is explicitly part of the string.
        // We use a standard Slack message format: "New Defect Reported: [ID] - [URL]"
        
        return String.format(
            "Defect Reported: %s | Severity: %s | GitHub Issue: %s",
            event.defectId(),
            severity,
            githubUrl
        );
    }
}
