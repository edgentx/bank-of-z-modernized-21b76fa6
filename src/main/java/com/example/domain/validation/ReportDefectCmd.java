package com.example.domain.validation;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect.
 * Triggered via temporal-worker exec (per S-FB-1).
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    String project,
    Map<String, String> metadata
) implements Command {}