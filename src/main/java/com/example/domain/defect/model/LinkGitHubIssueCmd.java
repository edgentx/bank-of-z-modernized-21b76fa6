package com.example.domain.defect.model;

import com.example.domain.shared.Command;

public record LinkGitHubIssueCmd(
    String defectId,
    String url
) implements Command {}