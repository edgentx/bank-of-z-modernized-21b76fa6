package com.example.domain.support.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect to the VForce360 system.
 * Triggers validation and Slack notification workflow.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String component,
    Map<String, String> context
) implements Command {}
