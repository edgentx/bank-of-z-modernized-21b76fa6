package com.example.workflows;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface for Defect Reporting operations.
 */
@ActivityInterface
public interface DefectReportActivities {
    void notifySlack(String defectId);
}
