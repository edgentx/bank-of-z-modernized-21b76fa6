package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggered by Temporal workflow or external API.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String severity,
        String component,
        String description
) implements Command {}
