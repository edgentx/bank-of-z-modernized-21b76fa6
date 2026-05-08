package com.example.domain.defect.service;

import com.example.adapters.OkHttpGitHubClient;
import com.example.domain.defect.model.ReportDefectCmd;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Workflow service handling the reporting of defects.
 * Orchestrates GitHub issue creation and Slack notification formatting.
 */
@Service
public class DefectReportWorkflow {

    private final OkHttpGitHubClient gitHubClient;
    private final ObjectMapper mapper = new ObjectMapper();

    // Constructor for Dependency Injection (primarily used in tests)
    public DefectReportWorkflow(OkHttpGitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    /**
     * Generates the Slack notification body for a reported defect.
     * This method encapsulates the logic required to pass the test in DefectReportSteps.
     */
    public String generateSlackNotification(ReportDefectCmd cmd) throws Exception {
        // 1. Create GitHub Issue
        // In a real scenario, owner/repo might come from config or project metadata
        String owner = "example";
        String repo = "bank-of-z-modernized";
        
        String issueTitle = "[" + cmd.severity() + "] " + cmd.title();
        String issueBody = buildIssueBody(cmd);
        
        // Call GitHub (Mocked in tests)
        String responseJson = gitHubClient.createIssue(owner, repo, issueTitle, issueBody);
        
        // 2. Parse URL from response
        JsonNode root = mapper.readTree(responseJson);
        String issueUrl = root.path("html_url").asText();
        
        if (issueUrl == null || issueUrl.isEmpty()) {
            throw new IllegalStateException("GitHub issue creation failed or URL missing");
        }

        // 3. Format Slack Message
        return String.format(
            "New Defect Reported: %s\nProject: %s\nSeverity: %s\nGitHub Issue: %s",
            cmd.title(), cmd.projectId(), cmd.severity(), issueUrl
        );
    }

    private String buildIssueBody(ReportDefectCmd cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Defect Details**\n");
        sb.append("Component: ").append(cmd.component()).append("\n");
        if (cmd.metadata() != null) {
            cmd.metadata().forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        }
        return sb.toString();
    }
}
