package com.example.ports;

import com.example.domain.reporting.model.ReportDefectCmd;

/**
 * Port for interacting with Temporal workflows.
 * This allows us to mock the Temporal worker execution in tests.
 */
public interface TemporalPort {
    /**
     * Simulates triggering the _report_defect workflow.
     * @param cmd The command to trigger the defect report.
     * @return The resulting message body that would be sent to Slack.
     */
    String executeReportDefectWorkflow(ReportDefectCmd cmd);
}
