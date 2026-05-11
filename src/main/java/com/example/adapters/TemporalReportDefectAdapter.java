package com.example.adapters;

import com.example.ports.ReportDefectPort;
import org.springframework.stereotype.Component;

/**
 * Production implementation of ReportDefectPort.
 * Formats the defect data into the Slack body message.
 * In a full implementation, this would trigger the Temporal workflow,
 * but for this validation story, we return the formatted string directly.
 */
@Component
public class TemporalReportDefectAdapter implements ReportDefectPort {

    @Override
    public String triggerDefectReport(String issueId, String title, String url) {
        // Validation logic matching the Green phase requirements
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be null or empty");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title cannot be null or empty");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("url cannot be null or empty");
        }

        // Construct the Slack body including the URL (Fixing VW-454)
        StringBuilder sb = new StringBuilder();
        sb.append("*Defect Reported*\n");
        sb.append("ID: ").append(issueId).append("\n");
        sb.append("Title: ").append(title).append("\n");
        
        // The Fix: Ensure the URL is appended to the body
        sb.append("Link: ").append(url).append("\n");
        
        return sb.toString();
    }
}
