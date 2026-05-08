package com.example.domain.defect.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect discovered in the VForce360 system.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String severity,
        String component,
        Map<String, String> metadata
) implements Command {
}
