package com.example.workflow;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface for side-effects.
 * Wraps the Ports (GitHubPort, SlackPort) for use within the Workflow.
 */
@ActivityInterface
public interface DefectActivities {

    /**
     * Creates an issue via GitHubPort.
     */
    String createGitHubIssue(String title, String body);

    /**
     * Sends a message via SlackPort.
     */
    void notifySlack(String defectTitle, String githubUrl);
}
