package com.example.mocks;

import com.example.ports.TemporalPort;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockTemporalAdapter implements TemporalPort {
    
    private final Map<String, String> workflowStatuses = new ConcurrentHashMap<>();
    private SlackPort slackPort;
    private GitHubPort gitHubPort;
    
    public MockTemporalAdapter() {
        // Default constructor
    }
    
    public void setSlackPort(SlackPort slackPort) {
        this.slackPort = slackPort;
    }
    
    public void setGitHubPort(GitHubPort gitHubPort) {
        this.gitHubPort = gitHubPort;
    }
    
    @Override
    public boolean executeReportDefect(String defectId) {
        if (defectId == null || defectId.isEmpty()) {
            throw new IllegalArgumentException("Defect ID cannot be null or empty");
        }
        
        // Simulate the workflow execution
        // In a real scenario, this would orchestrate:
        // 1. Create GitHub issue
        // 2. Send Slack notification with the GitHub URL
        
        String issueUrl = gitHubPort.createIssue(defectId, "Defect: " + defectId);
        
        // Format the Slack message with the GitHub URL
        String slackMessage = String.format(
            "New defect reported: %s\nGitHub Issue: <%s|View Issue>",
            defectId,
            issueUrl
        );
        
        boolean slackResult = slackPort.sendMessage("#vforce360-issues", slackMessage);
        
        if (slackResult) {
            workflowStatuses.put(defectId, "COMPLETED");
            return true;
        } else {
            workflowStatuses.put(defectId, "FAILED");
            return false;
        }
    }
    
    @Override
    public String getWorkflowStatus(String workflowId) {
        return workflowStatuses.getOrDefault(workflowId, "UNKNOWN");
    }
    
    /**
     * Clear all workflow statuses (useful between tests)
     */
    public void clear() {
        workflowStatuses.clear();
    }
}