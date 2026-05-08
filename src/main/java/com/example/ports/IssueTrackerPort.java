package com.example.ports;

/**
 * Port for creating issues in the external issue tracker (GitHub).
 */
public interface IssueTrackerPort {
    /**
     * Creates a remote issue for the given defect and returns the canonical URL.
     *
     * @param defectId The internal aggregate ID.
     * @param title The defect title.
     * @return The fully qualified URL to the issue (e.g., https://github.com/.../issues/123).
     */
    String createIssue(String defectId, String title);
}
