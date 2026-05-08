package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect detected by the validation engine.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCommand(
        String defectId,
        String severity,
        String summary,
        Map<String, Object> details
) implements Command {}
