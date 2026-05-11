package com.example.ports;

/**
 * Port interface for reporting defects to external systems (e.g., Temporal -> Slack).
 * This abstraction allows mocking in tests and concrete implementation in production.
 */
public interface ReportDefectPort {
    
    /**
     * Simulates the workflow that reports a defect.
     * 
     * @param issueId The ID of the issue (e.g., VW-454)
     * @param title The title of the defect
     * @param url The GitHub URL to the issue
     * @return The formatted Slack body string
     */
    String triggerDefectReport(String issueId, String title, String url);
}
