package com.example.domain.validation.model;

import com.example.domain.shared.Command;

public record LinkGitHubIssueCmd(String validationId, String issueUrl) implements Command {}
