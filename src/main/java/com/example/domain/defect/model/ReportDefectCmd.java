package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect via VForce360.
 * Triggered by Temporal or internal PM diagnostic conversation.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity
) implements Command {}
