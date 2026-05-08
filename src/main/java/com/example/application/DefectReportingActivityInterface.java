package com.example.application;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal Activity Interface.
 */
@ActivityInterface
public interface DefectReportingActivityInterface {
    @ActivityMethod
    String reportDefect(String title, String body);
}
