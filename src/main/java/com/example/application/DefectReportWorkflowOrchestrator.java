package com.example.application;

import com.example.workers.ReportDefectActivity;

/**
 * Orchestrator for the Defect Reporting Workflow.
 * This class acts as the Workflow implementation, coordinating the activity calls.
 * While Temporal Workflows are often interfaces, this Plain Java Object approach
 * satisfies the test requirements while fitting the Spring Boot Application structure.
 */
public class DefectReportWorkflowOrchestrator {

    private final ReportDefectActivity activity;

    public DefectReportWorkflowOrchestrator(ReportDefectActivity activity) {
        this.activity = activity;
    }

    /**
     * Reports a defect by invoking the necessary activities.
     *
     * @param title       Defect title
     * @param description Defect description
     * @param channel     Slack channel
     * @return The URL of the created GitHub issue.
     */
    public String reportDefect(String title, String description, String channel) {
        // Delegate to the activity which encapsulates the port interactions
        return activity.execute(title, description, channel);
    }
}