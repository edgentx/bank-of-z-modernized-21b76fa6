package com.example.workflows;

import com.example.vforce.github.model.GithubIssue;
import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface for side-effects.
 */
@ActivityInterface
public interface ReportDefectActivity {

    /**
     * Creates a ticket in GitHub.
     */
    GithubIssue createGithubIssue(String description);

    /**
     * Sends a notification to Slack.
     */
    void postSlackNotification(String message, GithubIssue issue);
}
