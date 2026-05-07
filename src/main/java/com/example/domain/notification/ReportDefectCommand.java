package com.example.domain.notification;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        Map<String, Object> payload
) implements Command {}
