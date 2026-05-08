package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 */
public record ReportDefectCmd(
    String summary,
    String description,
    String severity,
    String component
) implements Command {}
