package com.example.ports;

import com.example.slack.model.ReportDefectCmd;

/**
 * Port interface for reporting defects.
 * Implementations (e.g., Temporal workflow activities) will handle
 * the creation of tickets and formatting of Slack notifications.
 */
public interface ReportDefectPort {
    /**
     * Executes the report defect workflow logic.
     * Returns the formatted Slack body string for verification.
     */
    String executeReportDefectWorkflow(ReportDefectCmd cmd);
}
