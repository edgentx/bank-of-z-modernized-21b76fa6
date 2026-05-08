package com.example.workflow;

import com.example.domain.defect.model.ReportDefectCommand;

/**
 * Temporal Activity Interface for defect reporting.
 */
public interface DefectActivities {

    /**
     * Reports the defect to GitHub and sends a Slack notification.
     * @param cmd The command details.
     * @return The GitHub URL generated.
     */
    String reportDefect(ReportDefectCommand cmd);
}
