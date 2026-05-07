package com.example.domain.vforce.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect from VForce360 diagnostics.
 * Creates a GitHub issue and notifies a Slack channel.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component,
    String projectId
) implements Command {}
