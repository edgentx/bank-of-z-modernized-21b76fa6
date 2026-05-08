package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected by VForce360.
 * Used in Story S-FB-1 to validate defect reporting logic.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String project,
    String description
) implements Command {}
