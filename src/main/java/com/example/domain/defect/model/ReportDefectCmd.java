package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g., VW-454) to the VForce360 system.
 * This triggers the validation and GitHub/Slack workflow.
 */
public record ReportDefectCmd(
    String defectId,       // e.g., "VW-454"
    String title,
    String description,
    String severity,       // e.g., "LOW"
    String component       // e.g., "validation"
) implements Command {}
