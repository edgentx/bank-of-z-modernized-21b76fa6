package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Internal domain command to report a defect.
 * Wraps the external ReportDefectCmd to isolate the domain layer from DTOs.
 */
public record ReportDefectCommand(
        String defectId,
        String issueId,
        String summary
) implements Command {}