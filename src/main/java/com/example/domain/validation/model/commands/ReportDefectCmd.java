package com.example.domain.validation.model.commands;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String validationId,
    String defectId,
    String summary,
    String githubIssueUrl
) implements Command {}