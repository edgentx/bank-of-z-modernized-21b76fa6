package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record VerifySlackLinkCmd(String validationId, String slackBody, String targetUrl) implements Command {}
