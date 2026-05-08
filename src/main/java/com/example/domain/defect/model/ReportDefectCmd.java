package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Used by the Temporal workflow to initiate the reporting process.
 */
public record ReportDefectCmd(
    String projectId,
    String severity,
    String component,
    String title,
    String description
) implements Command {}
