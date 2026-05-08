package com.example.workflows;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ReportDefectActivities {
    String createGitHubIssue(String summary, String description);
    void notifySlack(String message);
}