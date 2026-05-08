package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the defect reporting workflow via Temporal.
 * This is the input payload for the saga/workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component
) implements Command {}
