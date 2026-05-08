package com.example.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DefectReportingActivitiesImpl {
    @ActivityMethod
    String createGitHubIssue(String description);

    @ActivityMethod
    void notifySlack(String messageBody);
}