package com.example.activities;

public interface DefectReportingActivities {
    String createGitHubIssue(String description);
    void notifySlack(String messageBody);
}