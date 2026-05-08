package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with Issue Tracking systems (e.g., GitHub, Jira).
 */
public interface IssueTrackingPort {

    /**
     * Creates a remote issue based on the defect details.
     *
     * @param defectDetails The raw details of the defect.
     * @return An Optional containing the URL of the created issue, or empty if creation failed/was skipped.
     */
    Optional<String> createRemoteIssue(String defectDetails);
}
