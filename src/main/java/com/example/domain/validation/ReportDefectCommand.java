package com.example.domain.validation;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect (VW-454 scenario).
 * Triggered via Temporal-worker exec.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String description,
        String projectId
) implements Command {}
