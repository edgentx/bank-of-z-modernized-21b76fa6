package com.example.workflows;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface for side-effects such as DB writes or Slack notifications.
 */
@ActivityInterface
public interface ReportDefectActivity {

    String createGitHubIssue(String description, String severity);

    void notifySlack(String text);
}