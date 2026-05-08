package com.example.workers;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal Activity Interface for Defect Reporting.
 * Wraps external port calls to ensure they are executed within the activity context.
 */
@ActivityInterface
public interface ReportDefectActivity {

    @ActivityMethod
    String reportDefect(String summary, String description, String slackChannel);
}