package com.example.mocks;

import com.example.ports.TemporalWorkflowPort;
import org.springframework.stereotype.Component;

@Component
public class MockTemporalWorkflowPort implements TemporalWorkflowPort {

    @Override
    public void triggerReportDefect(String issueId, String description) {
        // Simulate triggering the workflow
        System.out.println("[MockTemporal] Triggering report_defect for " + issueId);
    }
}
