package com.example.application;

import com.example.vforce.shared.ReportDefectCommand;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal Activity Interface for Defect Reporting.
 * This fixes the build error regarding missing interfaces.
 */
@ActivityInterface
public interface DefectReportingActivities {
    
    @ActivityMethod
    void reportDefect(ReportDefectCommand command);
}