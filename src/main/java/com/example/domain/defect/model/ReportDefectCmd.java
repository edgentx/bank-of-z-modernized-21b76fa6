package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect. Triggered by Temporal workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity
) implements Command {}
