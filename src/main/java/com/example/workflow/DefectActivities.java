package com.example.workflow;

import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity interface for defect reporting side-effects.
 */
@ActivityInterface
public interface DefectActivities {

    /**
     * Formats and sends a Slack message for the reported defect.
     *
     * @param command The defect details.
     */
    void notifySlack(ReportDefectCommand command);
}
