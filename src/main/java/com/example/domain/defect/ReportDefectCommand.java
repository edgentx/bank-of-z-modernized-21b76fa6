package com.example.domain.defect;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to report a defect.
 */
public record ReportDefectCommand(String defectId, String gitHubUrl) implements Command {
    public ReportDefectCommand {
        Objects.requireNonNull(defectId, "defectId must not be null");
        Objects.requireNonNull(gitHubUrl, "gitHubUrl must not be null");
    }
}