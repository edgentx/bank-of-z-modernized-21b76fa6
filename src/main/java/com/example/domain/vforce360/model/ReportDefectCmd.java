package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect from VForce360 diagnostics.
 * ID: VW-454
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity,
        String projectId
) implements Command {
}