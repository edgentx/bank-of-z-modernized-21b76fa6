package com.example.workers;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Activity Interface for reporting defects.
 * Implementations will interact with GitHub and Slack ports.
 */
@ActivityInterface
public interface ReportDefectActivity {

    @ActivityMethod
    String createGitHubIssue(String title, String description, String component);

    @ActivityMethod
    void sendSlackNotification(String channel, String messageBody);
}