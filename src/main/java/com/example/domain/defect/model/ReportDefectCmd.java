package com.example.domain.defect.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String component,
    String projectId,
    String githubIssueUrl // The URL returned by GitHub API after creating the issue
) implements Command {}
