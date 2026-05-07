package com.example.domain.notification.model;

import com.example.domain.shared.Command;

import java.util.StringJoiner;

/**
 * Command to report a defect.
 */
public record ReportDefectCmd(
    String notificationId,
    String title,
    String description,
    String githubIssueUrl
) implements Command {
    public ReportDefectCmd {
        if (notificationId == null || notificationId.isBlank()) {
            throw new IllegalArgumentException("notificationId cannot be null");
        }
    }
}
