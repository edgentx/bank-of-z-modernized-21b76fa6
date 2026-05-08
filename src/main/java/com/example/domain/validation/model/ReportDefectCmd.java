package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect to Slack.
 * Context: VW-454 Fix - GitHub URL validation.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    Map<String, String> metadata // Contains GitHub URL if present
) implements Command {}
