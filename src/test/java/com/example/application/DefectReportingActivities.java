package com.example.application;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Activity Interface definition for Temporal.
 */
@ActivityInterface
public interface DefectReportingActivities {
    @ActivityMethod
    String execute(String title, String description);
}