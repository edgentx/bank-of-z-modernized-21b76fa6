package com.example.domain.vforce;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * Part of the VForce360 integration.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String severity,
    String projectId,
    Map<String, String> context
) implements Command {}
