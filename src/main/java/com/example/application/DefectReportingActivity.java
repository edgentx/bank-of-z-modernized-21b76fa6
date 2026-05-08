package com.example.application;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface.
 */
@ActivityInterface
public interface DefectReportingActivity {
    String createGitHubIssue(String title, String body);
    void notifySlack(String githubUrl);
}
