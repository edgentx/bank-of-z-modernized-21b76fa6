package com.example.ports;

/**
 * Port interface for reporting defects to external issue trackers (e.g., GitHub).
 */
public interface IssueTrackerPort {
    
    /**
     * Reports a defect and returns the URL of the created issue.
     *
     * @param projectId The ID of the project
     * @param summary   Summary of the defect
     * @param description Full description of the defect
     * @return The URL string to the created issue
     */
    String reportDefect(String projectId, String summary, String description);
}
