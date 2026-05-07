package com.example.domain.validation;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect identified in the VForce360 system.
 * Part of the Validation Aggregate.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    Map<String, String> context
) implements Command {
}
