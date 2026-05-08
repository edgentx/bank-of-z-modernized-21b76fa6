package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String validationId,
    String summary,
    String description,
    String severity
) implements Command {}
