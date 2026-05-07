package com.example.domain.vforce.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity, // LOW, MEDIUM, HIGH, CRITICAL
    String type      // BUG, DEFECT, FEATURE
) implements Command {}
