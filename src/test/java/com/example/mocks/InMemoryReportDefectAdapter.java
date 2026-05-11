package com.example.mocks;

import com.example.ports.ReportDefectPort;

/**
 * Mock adapter for the ReportDefectPort.
 * Simulates the Temporal worker execution for testing without real I/O.
 */
public class InMemoryReportDefectAdapter implements ReportDefectPort {

    @Override
    public String triggerDefectReport(String issueId, String title, String url) {
        // This implementation is intentionally a STUB to fail the test initially.
        // It returns a body that is MISSING the URL, fulfilling the "Red Phase" requirement.
        
        StringBuilder sb = new StringBuilder();
        sb.append("*Defect Reported*\n");
        sb.append("ID: ").append(issueId).append("\n");
        sb.append("Title: ").append(title).append("\n");
        
        // BUG: Missing the URL line here. 
        // sb.append("Link: ").append(url).append("\n"); 
        
        return sb.toString();
    }
}
