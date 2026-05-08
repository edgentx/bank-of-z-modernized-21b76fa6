package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the reporting of a defect.
 * Associated with Story S-FB-1 / Defect VW-454.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String description,
        String severity
) implements Command {}
