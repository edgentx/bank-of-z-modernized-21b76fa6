package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger a defect report.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}
