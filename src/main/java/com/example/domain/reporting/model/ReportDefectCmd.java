package com.example.domain.reporting.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubIssueUrl,
    String severity
) implements Command {}
