package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(
    String validationId,
    String severity,
    String component,
    String description
) implements Command {}
