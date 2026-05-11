package com.example.mocks;

import com.example.ports.ReportDefectPort;
import com.example.slack.model.ReportDefectCmd;

/**
 * Mock adapter for ReportDefectPort.
 * In the RED phase, this might simulate the missing implementation,
 * or simulate a failure state.
 */
public class ReportDefectHandlerMock implements ReportDefectPort {

    @Override
    public String executeReportDefectWorkflow(ReportDefectCmd cmd) {
        // RED PHASE:
        // This implementation is intentionally simplistic or incorrect
        // to force the test to drive the development.
        // If we return "Body without URL", the test should fail.
        return "Defect Reported: " + cmd.defectId();
    }

    /**
     * A nested static class to simulate a specific bad behavior (e.g. wrong URL format)
     * for negative testing.
     */
    public static class FailingMock implements ReportDefectPort {
        @Override
        public String executeReportDefectWorkflow(ReportDefectCmd cmd) {
            return "http://localhost:8080/issues/" + cmd.defectId();
        }
    }
}
