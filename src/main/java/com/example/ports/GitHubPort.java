package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for creating GitHub issues.
 * Used to verify VW-454 integration.
 */
public interface GitHubPort {
    String createIssue(Command cmd);
}