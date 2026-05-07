package com.example.e2e.regression;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Simulation Service for the Temporal Worker logic.
 * In the real system, this logic resides within a Temporal Activity/Workflow implementation.
 * For the purpose of E2E testing the defect fix, we stub the orchestration logic here.
 * <p>
 * This class represents the "Existing System" logic that is currently broken
 * or needs to be verified.
 */
@Service
public class TemporalDefectReportSimulationService {

    private final GitHubIssuePort gitHubPort;
    private final SlackNotificationPort slackPort;

    public TemporalDefectReportSimulationService(GitHubIssuePort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Simulates the execution of the '_report_defect' workflow.
     * <p>
     * Current State (Pre-Fix): This method might just post a generic message,
     * or fail to include the URL in the body.
     * Target State (Post-Fix): This method MUST include the GitHub URL in the body.
     */
    public void executeReportDefectWorkflow(String issueId, String channelId) {
        // Step 1: Retrieve GitHub URL
        String url = gitHubPort.getIssueUrl(issueId);

        // Step 2: Construct Slack Body
        // TODO: IMPLEMENT FIX HERE. Currently just says "Defect Reported".
        // The test expects 'url' to be part of this string.
        String messageBody = "Defect Reported: " + issueId + ". See GitHub for details."; // BROKEN IMPLEMENTATION

        // Step 3: Post to Slack
        slackPort.postMessage(channelId, messageBody);
    }
}
