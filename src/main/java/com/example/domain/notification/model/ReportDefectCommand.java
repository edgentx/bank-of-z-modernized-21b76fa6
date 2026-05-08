package com.example.domain.notification.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect.
 * Represents the input for the VW-454 scenario.
 */
public record ReportDefectCommand(
    String title,
    String description,
    String severity,
    String component,
    Map<String, String> metadata
) implements Command {}
