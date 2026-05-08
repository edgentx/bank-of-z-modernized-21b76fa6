package com.example.domain.reporting.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect to the VForce360 system.
 * This will trigger a Temporal workflow which eventually posts to Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String severity,
    String component,
    String description,
    Map<String, String> metadata
) implements Command {}
