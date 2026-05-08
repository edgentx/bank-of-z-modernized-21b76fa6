package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}
