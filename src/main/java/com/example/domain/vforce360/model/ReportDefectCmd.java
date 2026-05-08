package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to report a defect from VForce360 PM diagnostic conversation.
 * Part of the aggregate handling defect tracking.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String projectId,
    String severity,
    Instant occurredAt
) implements Command {
    public ReportDefectCmd {
        Objects.requireNonNull(defectId);
        Objects.requireNonNull(title);
    }
}
