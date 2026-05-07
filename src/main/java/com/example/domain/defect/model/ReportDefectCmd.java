package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;
import java.util.Objects;

/**
 * Command to report a defect.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity,
        String githubIssueUrl,
        Map<String, String> metadata
) implements Command {

    public ReportDefectCmd {
        Objects.requireNonNull(defectId, "defectId is required");
        Objects.requireNonNull(title, "title is required");
        // GitHub URL is the critical field for this regression test
        if (githubIssueUrl != null && !githubIssueUrl.startsWith("https://github.com")) {
             throw new IllegalArgumentException("githubIssueUrl must be a valid GitHub URL");
        }
    }
}