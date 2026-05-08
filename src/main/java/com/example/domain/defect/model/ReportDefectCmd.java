package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered during a migration or diagnostic conversation.
 * Mirrors the structure found in other domain commands (e.g., EnrollCustomerCmd).
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity,
        String projectId
) implements Command {}
