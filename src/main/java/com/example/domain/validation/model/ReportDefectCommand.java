package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCommand(
    String reportId,
    String githubUrl,
    String slackBody
) implements Command {}