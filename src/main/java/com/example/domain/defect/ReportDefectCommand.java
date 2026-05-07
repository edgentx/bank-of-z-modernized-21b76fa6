package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to VForce360 and notify Slack.
 * Part of the defect reporting sub-domain.
 */
public record ReportDefectCommand(
    String title,
    String description,
    String severity
) implements Command {}
