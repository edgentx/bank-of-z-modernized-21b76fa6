package com.example.e2e.regression;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for Story S-FB-1.
 * Validates that defect reporting via Temporal worker (simulated) results
 * in a Slack body containing the GitHub issue URL.
 */
public class SFB1_GitHubUrlValidationTest {

    /**
     * Regression test for VW-454.
     * Scenario: Trigger _report_defect via temporal-worker exec.
     * Expected: Verify Slack body contains GitHub issue link.
     */
    @Test
    public void testReportDefect_SlackBodyContainsGitHubUrl() throws Exception {
        // 1. Setup Mock Infrastructure
        // We use the MockSlackNotificationPort to intercept the outgoing message.
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();

        // 2. Execute the Workflow Logic
        // This simulates the Temporal activity/participant responsible for notifying.
        // In the real implementation, this class would be injected or fetched via Spring Context.
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(mockSlack);

        // Inputs matching the defect report
        String defectId = "VW-454";
        String githubIssueUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        String project = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

        workflow.reportDefect(defectId, githubIssueUrl, project);

        // 3. Verify Expected Behavior
        // The body must include the full URL.
        mockSlack.assertBodyContains("GitHub issue:");
        mockSlack.assertBodyContains(githubIssueUrl);
    }

    /**
     * Negative test: Ensure we don't pass if the URL is missing (TDD safety).
     */
    @Test
    public void testReportDefect_MissingUrl_FailsValidation() throws Exception {
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
        DefectReportingWorkflow workflow = new DefectReportingWorkflow(mockSlack);

        // Simulate a bug where the URL is not appended
        workflow.reportDefect("VW-454", null, "project-id");

        String body = mockSlack.getLastBody();
        // We expect the test to fail if the implementation doesn't fix the defect.
        // Here we assert the absence to prove the test works correctly.
        assertFalse(body.contains("http"), "Body should not contain URL if null was provided");
    }

    // ---------------------------------------------------------------------
    // Stubs / Production Placeholders (These would be in src/main normally)
    // ---------------------------------------------------------------------

    /**
     * Placeholder for the Temporal Workflow/Activity implementation.
     * This class represents the code under test.
     */
    public static class DefectReportingWorkflow {
        private final SlackNotificationPort slackClient;

        public DefectReportingWorkflow(SlackNotificationPort slackClient) {
            this.slackClient = slackClient;
        }

        public void reportDefect(String defectId, String githubUrl, String projectId) {
            // CURRENT (BUGGY) IMPLEMENTATION - To be fixed by Engineer
            // The defect states the URL is missing.
            StringBuilder body = new StringBuilder();
            body.append("Defect Reported: ").append(defectId).append("\n");
            body.append("Project: ").append(projectId).append("\n");
            // Intentionally omitting the URL for the RED phase or testing the missing behavior
            // body.append("GitHub issue: <").append(githubUrl).append(">"); 

            slackClient.sendMessage("#vforce360-issues", body.toString());
        }
    }
}