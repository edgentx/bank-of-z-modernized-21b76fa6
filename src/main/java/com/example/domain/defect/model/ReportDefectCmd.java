package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl
) implements Command {}
