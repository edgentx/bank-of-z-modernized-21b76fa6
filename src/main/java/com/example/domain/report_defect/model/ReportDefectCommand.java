package com.example.domain.report_defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command representing a request to report a defect via VForce360.
 * Triggered by the temporal-worker.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    Map<String, String> metadata
) implements Command {}
