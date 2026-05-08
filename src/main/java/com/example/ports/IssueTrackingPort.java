package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with external Issue Tracking systems (e.g. GitHub/JIRA).
 */
public interface IssueTrackingPort {

    /**
     * Creates a remote issue based on the defect details.
     *
     * @param title The title of the defect
     * @param description The description/body of the defect
     * @return The URL of the created issue
     */
    String createIssue(String title, String description);
}
