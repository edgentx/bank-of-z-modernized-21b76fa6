package com.example.activities;

/**
 * Port interface for Defect Reporting activities.
 * Decouples the workflow logic from the actual implementation of external communications.
 */
public interface DefectReportingActivities {
    String createGitHubIssue(String description);
    void notifySlack(String messageBody);
}