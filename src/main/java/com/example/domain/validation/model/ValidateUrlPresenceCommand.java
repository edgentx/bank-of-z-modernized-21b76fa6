package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ValidateUrlPresenceCommand(
    String validationId,
    String expectedUrl,
    String actualContent
) implements Command {}
