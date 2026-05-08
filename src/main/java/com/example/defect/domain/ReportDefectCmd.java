package com.example.defect.domain;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * Used by the Temporal workflow to trigger the defect reporting process.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String projectId,
    Map<String, String> metadata
) implements Command {}
