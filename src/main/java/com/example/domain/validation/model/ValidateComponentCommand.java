package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record ValidateComponentCommand(String validationId, String component) implements Command {}
