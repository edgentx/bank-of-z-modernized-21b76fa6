package com.example.ports;

import java.util.Map;

/**
 * Port for creating issues in an external tracker (e.g. GitHub/Jira).
 * This interface must be implemented by the production adapter and mocked in tests.
 */
public interface IssueTrackerPort {

    /**
     * Creates a new issue ticket.
     *
     * @param title The title of the defect
     * @param body The body content of the defect
     * @return A Map containing the response details, specifically 'url'
     */
    Map<String, String> createIssue(String title, String body);
}
