package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record RecordValidationResultCmd(
    String validationId,
    String component,
    boolean passed,
    String reason
) implements Command {}