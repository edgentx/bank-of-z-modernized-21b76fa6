package com.example.application;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal Activity Interface for Defect Reporting.
 */
@ActivityInterface
public interface DefectReportingActivities {

    @ActivityMethod
    void reportDefect(String defectDetails);
}
