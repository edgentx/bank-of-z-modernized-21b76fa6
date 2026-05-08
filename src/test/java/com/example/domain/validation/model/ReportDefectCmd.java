package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * This command triggers the defect reporting workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}
