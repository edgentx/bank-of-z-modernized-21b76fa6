package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to trigger the defect reporting workflow.
 * Corresponds to triggering _report_defect via temporal-worker exec.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity,
        String component,
        Map<String, Object> metadata
) implements Command {
}