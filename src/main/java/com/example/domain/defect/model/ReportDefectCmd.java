package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Part of the VForce360 integration workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String projectId
) implements Command {}
