package com.example.ports;

/** Port for creating GitHub issues. */
public interface GitHubPort {
    /** Creates a remote issue and returns the URL. */
    String createIssue(String title, String body);
}
