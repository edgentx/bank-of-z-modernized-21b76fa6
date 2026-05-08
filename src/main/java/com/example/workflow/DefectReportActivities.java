package com.example.workflow;

import com.example.domain.validation.model.GitHubIssueUrl;
import com.example.domain.validation.model.SlackMessageBody;
import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity Interface for side-effects.
 * Interacts with external ports (GitHub, Slack).
 */
@ActivityInterface
public interface DefectReportActivities {

    /**
     * Activity to create an issue on GitHub.
     */
    GitHubIssueUrl createGitHubIssue(String title, String description);

    /**
     * Activity to send a notification to Slack.
     */
    void sendSlackNotification(SlackMessageBody body);
}
