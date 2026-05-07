package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect via VForce360 diagnostics.
 * This triggers the Temporal workflow that posts to Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    Map<String, String> metadata
) implements Command {}
