package com.example.ports;

import com.example.domain.validation.model.IssueUrl;

/**
 * Port for interacting with external issue tracking systems (e.g., GitHub).
 */
public interface IssueTrackerPort {
    /**
     * Creates a new issue in the tracker system.
     * @param title Title of the issue
     * @param body Body content of the issue
     * @return The URL of the created issue
     */
    IssueUrl createIssue(String title, String body);
}
