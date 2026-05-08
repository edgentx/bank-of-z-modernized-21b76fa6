package com.example.application;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

/**
 * Activity Interface for Defect Reporting operations (Slack, GitHub, etc.).
 * This interface defines the contract between the Temporal Workflow and the actual business logic.
 */
@ActivityInterface
public interface DefectReportingActivity {

    /**
     * Posts a message to Slack with the provided body.
     * @param channel The Slack channel ID or name.
     * @param body The message body content.
     * @return true if posting was successful.
     */
    @ActivityMethod
    boolean postToSlack(String channel, String body);

    /**
     * Creates an issue on GitHub and returns the URL.
     * @param title The issue title.
     * @param body The issue body.
     * @return The HTML URL of the created issue.
     */
    @ActivityMethod
    String createGitHubIssue(String title, String body);
}