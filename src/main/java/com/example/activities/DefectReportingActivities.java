package com.example.activities;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity Interface.
 * Defines the contract for the defect reporting workflow step.
 */
@ActivityInterface
public interface DefectReportingActivities {

    /**
     * Reports a defect to the external systems (Slack + Issue Tracker).
     *
     * @param issueId The ID of the defect (e.g., "VW-454").
     */
    void reportDefect(String issueId);
}