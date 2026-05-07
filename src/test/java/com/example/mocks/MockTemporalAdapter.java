package com.example.mocks;

import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.ports.TemporalPort;

import java.util.Map;
import java.util.HashMap;

/**
 * Mock implementation of TemporalPort for testing.
 * This simulates the behavior of the temporal worker exec without
 * actually connecting to Temporal or Slack.
 */
public class MockTemporalAdapter implements TemporalPort {

    private String githubUrlBase = "https://github.com/bank-of-z/issues/";
    private boolean failExecution = false;

    @Override
    public String executeReportDefectWorkflow(ReportDefectCmd cmd) {
        if (failExecution) {
            throw new RuntimeException("Temporal execution failed");
        }

        // Simulate the logic that constructs the Slack body.
        // Ideally, this should include the link to the GitHub issue.
        StringBuilder body = new StringBuilder();
        body.append("*Defect Reported: ").append(cmd.title()).append("*\n");
        body.append(cmd.description()).append("\n");
        
        // DEFECT (VW-454): The code below is currently missing or incorrectly implemented.
        // We expect the URL to be formed using the defectId.
        String expectedUrl = githubUrlBase + cmd.defectId();
        
        // For the purpose of the Red/Green test, we return a body that LACKS the URL
        // to prove the test fails initially.
        // Implementation: body.append("View Issue: ").append(expectedUrl); 
        
        return body.toString();
    }

    // Helper to configure the mock
    public void setGithubUrlBase(String url) {
        this.githubUrlBase = url;
    }

    public void setFailExecution(boolean fail) {
        this.failExecution = fail;
    }
}
