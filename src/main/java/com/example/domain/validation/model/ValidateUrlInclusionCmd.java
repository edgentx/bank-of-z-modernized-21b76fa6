package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ValidateUrlInclusionCmd(
    String validationId,
    String textToValidate,
    String requiredUrl
) implements Command {}
