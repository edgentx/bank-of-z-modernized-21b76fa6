package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect detected by the VForce360 PM diagnostic system.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String severity,
        String component,
        Map<String, Object> context
) implements Command {
}
