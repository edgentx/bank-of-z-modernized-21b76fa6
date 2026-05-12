package com.example.ports;

/**
 * Outbound port for creating GitHub issues from the reporting bounded context.
 * Returns the canonical HTML URL of the created issue.
 */
public interface GitHubIssuePort {
  String createIssue(String title, String body);
}
