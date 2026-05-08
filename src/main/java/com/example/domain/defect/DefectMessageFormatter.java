package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import org.springframework.stereotype.Component;

import java.util.Formatter;

/**
 * Formatter for constructing the Slack notification body.
 * Centralizes the logic for creating the GitHub URL.
 */
@Component
public class DefectMessageFormatter {

    // In a real production app, this might come from application.properties
    private static final String GITHUB_ISSUE_BASE_URL = "https://github.com/example-org/vforce360/issues";

    /**
     * Formats the Slack message body including the GitHub issue URL.
     *
     * @param cmd The defect command.
     * @return A formatted string ready for Slack.
     */
    public String formatReportBody(ReportDefectCmd cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append("Defect Reported: ").append(cmd.title()).append("\n");
        sb.append("Severity: ").append(cmd.severity()).append("\n");
        sb.append("Component: ").append(cmd.component()).append("\n");
        
        // THE FIX for VW-454: Ensure the GitHub URL is present.
        // We construct a URL to the specific issue ID.
        String issueUrl = GITHUB_ISSUE_BASE_URL + "/" + cmd.defectId();
        sb.append("GitHub Issue: <").append(issueUrl).append(">\n");
        
        return sb.toString();
    }
}
