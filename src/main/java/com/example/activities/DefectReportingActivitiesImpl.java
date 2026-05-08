package com.example.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal Activity Interface definition.
 * This defines the contract that the Temporal Worker executes.
 * The implementation logic (Adapter) is wired separately in Spring configuration.
 */
@ActivityInterface
public interface DefectReportingActivitiesImpl extends DefectReportingActivities {
    // Inherits methods: createGitHubIssue, notifySlack
}