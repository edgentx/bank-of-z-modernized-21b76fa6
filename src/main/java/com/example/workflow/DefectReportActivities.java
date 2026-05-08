package com.example.workflow;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface DefectReportActivities {

    String createGitHubIssue(String title, String description, String severity);

    void sendSlackNotification(String messageBody);
}