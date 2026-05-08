package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect via the VForce360 diagnostic workflow.
 * Part of Story S-FB-1: Validating VW-454.
 */
public record ReportDefectCmd(
        String defectId,
        String description,
        String severity,
        String githubUrl
) implements Command {}
