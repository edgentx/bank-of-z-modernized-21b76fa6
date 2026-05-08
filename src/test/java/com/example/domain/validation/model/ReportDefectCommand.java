package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (VW-454).
 * Bridges the Temporal workflow execution to the domain logic.
 */
public record ReportDefectCommand(
    String projectId,
    String title,
    String description
) implements Command {}
