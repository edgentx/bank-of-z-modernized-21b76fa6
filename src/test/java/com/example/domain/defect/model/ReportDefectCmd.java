package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect. Modeled after the temporal trigger
 * '_report_defect'.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component,
    String projectId
) implements Command {}
