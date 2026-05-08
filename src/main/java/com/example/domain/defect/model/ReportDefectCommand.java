package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the defect reporting workflow.
 * Bridges the Temporal activity to the domain logic.
 */
public record ReportDefectCommand(
        String defectId,
        String channel
) implements Command {}