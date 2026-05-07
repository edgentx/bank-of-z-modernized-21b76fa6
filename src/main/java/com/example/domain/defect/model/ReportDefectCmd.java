package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to report a defect.
 * Primarily used by Temporal workflows to initiate the reporting process.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String githubUrl
) implements Command {
    public ReportDefectCmd {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("githubUrl cannot be null or blank for VW-454 compliance");
        }
    }
}