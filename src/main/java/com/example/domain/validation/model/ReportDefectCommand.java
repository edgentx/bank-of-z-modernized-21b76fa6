package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected in the VForce360 system.
 * This triggers the validation and Slack notification flow.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}
