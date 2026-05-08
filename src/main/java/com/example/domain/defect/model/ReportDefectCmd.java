package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect detected in the VForce360 PM diagnostic flow.
 * Corresponds to the temporal-worker exec trigger "_report_defect".
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    Map<String, String> context
) implements Command {}
