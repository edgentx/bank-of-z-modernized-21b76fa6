package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Encapsulates the data required to record a defect in the system.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}
