package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to report a defect via the VForce360 workflow.
 * Triggered by Temporal worker.
 */
public record ReportDefectCmd(
    String projectId,
    String title,
    String description,
    Instant occurredAt
) implements Command {
    public ReportDefectCmd {
        Objects.requireNonNull(projectId, "projectId required");
        Objects.requireNonNull(title, "title required");
        // description can be blank? Assuming no for now based on defect context.
    }
}
