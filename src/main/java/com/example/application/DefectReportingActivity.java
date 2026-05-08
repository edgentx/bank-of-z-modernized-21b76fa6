package com.example.application;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface for Defect Reporting.
 * Wraps the business logic use case.
 */
@ActivityInterface
public interface DefectReportingActivity {

    /**
     * Executes the reporting logic: Domain validation + Slack notification.
     *
     * @param defectId The ID of the defect.
     * @param githubUrl The GitHub URL to validate and include.
     * @param slackChannel The target channel.
     * @return Result status.
     */
    String reportToVForce360(String defectId, String githubUrl, String slackChannel);
}
