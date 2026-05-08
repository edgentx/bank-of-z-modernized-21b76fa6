package com.example.validation.ports;

import com.example.validation.domain.model.DefectReport;
import com.example.validation.domain.model.GitHubIssueLink;

public interface GitHubPort {
    GitHubIssueLink createIssue(DefectReport report);
}
