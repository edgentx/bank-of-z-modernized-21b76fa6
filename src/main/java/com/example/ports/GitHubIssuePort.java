package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Abstracted to allow mocking during the testing of defect reporting workflows.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The description of the issue.
     * @return The URL of the created issue, or empty if creation failed.
     */
    Optional<String> createIssue(String title, String body);
}
