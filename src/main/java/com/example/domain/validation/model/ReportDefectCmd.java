package com.example.domain.validation.model;

import com.example.domain.shared.Command;
import java.time.Instant;

public record ReportDefectCmd(
    String validationId,
    String title,
    Severity severity,
    String component,
    Instant occurredAt
) implements Command {}