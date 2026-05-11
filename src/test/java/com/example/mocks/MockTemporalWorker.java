package com.example.mocks;

import com.example.ports.TemporalReportDefectPort;

/**
 * Mock adapter for Temporal Report Defect logic.
 * Allows triggering the workflow logic directly in memory without a Temporal server.
 * In a real scenario, this would wrap the Workflow implementation stub.
 */
public class MockTemporalWorker {

    private final TemporalReportDefectPort workflowLogic;

    public MockTemporalWorker(TemporalReportDefectPort workflowLogic) {
        this.workflowLogic = workflowLogic;
    }

    public void executeReportDefect(String defectId, String title, String description) {
        // Directly invoke the port method (simulating the Workflow execution)
        workflowLogic.reportDefect(defectId, title, description);
    }
}
