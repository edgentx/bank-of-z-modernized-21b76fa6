package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggered via Temporal workflow or API.
 */
public record ReportDefectCmd(
    String defectId,
    String code,        // e.g., "VW-454"
    String summary,
    String severity     // "LOW", "MEDIUM", "HIGH"
) implements Command {}
