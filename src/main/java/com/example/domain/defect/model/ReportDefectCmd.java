package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String githubUrl,
        String channel
) implements Command {}
