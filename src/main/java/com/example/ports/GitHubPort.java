package com.example.ports;

import com.example.vforce.github.IssueLink;
import com.example.vforce.shared.ReportDefectCommand;

/**
 * Port interface for creating GitHub issues.
 * Allows mocking in tests without real HTTP calls.
 */
public interface GitHubPort {
    IssueLink createIssue(ReportDefectCommand command);
}