package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Part of the Domain Layer.
 */
public record ReportDefectCommand(
        String defectId,
        String issueId,
        String githubUrl,
        String summary
) implements Command {}
