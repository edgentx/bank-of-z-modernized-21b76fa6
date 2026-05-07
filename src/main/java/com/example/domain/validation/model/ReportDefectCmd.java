package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Set;

/**
 * Command to report a defect (e.g., VW-454) and generate a GitHub issue link.
 */
public record ReportDefectCmd(
    String validationId,
    String title,
    String githubIssueUrl
) implements Command {}
