package com.example.workflow;

import com.example.domain.shared.DefectReportedEvent;

/**
 * Workflow interface for reporting defects.
 * This is the Temporal Workflow definition.
 */
public interface ReportDefectWorkflow {

    /**
     * Reports a defect by creating a GitHub issue and notifying Slack.
     *
     * @param event The defect event containing details.
     */
    void reportDefect(DefectReportedEvent event);
}
