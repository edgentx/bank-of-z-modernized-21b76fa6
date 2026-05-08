package com.example.ports.secondary;

/**
 * Port interface for reporting defects.
 * This is the use case interface triggered by Temporal or other workers.
 */
public interface ReportDefectPort {
    
    /**
     * Reports a defect which involves generating a message and sending it via Slack.
     * 
     * @param title The title of the defect (e.g. VW-454)
     * @param url The URL to the GitHub issue.
     * @param details Additional details about the defect.
     */
    void reportDefect(String title, String url, String details);
}