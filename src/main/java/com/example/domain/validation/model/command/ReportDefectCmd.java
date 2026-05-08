package com.example.domain.validation.model.command;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String aggregateId,
    String githubUrl,
    String severity,
    String component
) implements Command {}
