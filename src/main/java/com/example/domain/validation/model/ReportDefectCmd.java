package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * Triggered by temporal-worker exec.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    Map<String, Object> payload
) implements Command {}