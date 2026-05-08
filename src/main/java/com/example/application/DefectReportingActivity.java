package com.example.application;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Activity interface for interacting with external systems like Slack.
 * Temporal ensures these are retryable and durable.
 */
@ActivityInterface
public interface DefectReportingActivity {
    @ActivityMethod
    boolean notifySlack(String defectId, String messageBody);
}