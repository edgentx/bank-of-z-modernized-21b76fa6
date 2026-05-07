package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect via the temporal-worker.
 * Part of the Validation Aggregate logic.
 */
public record ReportDefectCmd(
    String defectTitle,
    String defectBody
) implements Command {}
