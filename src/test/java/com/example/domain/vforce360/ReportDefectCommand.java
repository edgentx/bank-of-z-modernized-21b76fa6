package com.example.domain.vforce360;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Part of the VForce360 integration domain model.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId
) implements Command {
}
