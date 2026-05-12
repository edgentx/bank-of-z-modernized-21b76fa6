package com.example.ports;

public interface GitHubIssuePort {
  /** Open or fetch a GitHub issue for a reported defect and return its URL. */
  String openIssue(String title, String body, String severity);
}
