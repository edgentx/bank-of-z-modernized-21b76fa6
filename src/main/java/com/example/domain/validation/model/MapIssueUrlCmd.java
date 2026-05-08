package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record MapIssueUrlCmd(
    String validationId,
    String url
) implements Command {}