package com.example.application.reporting;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to report a defect to VForce360 monitoring.
 * Triggered by Temporal workflows.
 */
public record ReportDefectCmd(
    String issueId,
    String title,
    String description,
    String severity
) implements Command {

    public ReportDefectCmd {
        Objects.requireNonNull(issueId, "issueId is required");
        Objects.requireNonNull(title, "title is required");
        Objects.requireNonNull(severity, "severity is required");
    }
}
