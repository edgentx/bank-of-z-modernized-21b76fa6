package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the defect reporting workflow.
 * This command would typically originate from a Temporal workflow execution
     */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}
