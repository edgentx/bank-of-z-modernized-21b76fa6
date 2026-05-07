package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to trigger the reporting of a defect.
 * This would typically be triggered by a Temporal workflow or an external diagnostic process.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}
