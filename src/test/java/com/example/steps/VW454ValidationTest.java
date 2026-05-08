package com.example.steps;

import com.example.Application;
import com.example.mocks.MockGitHubAdapter;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454.
 * Verifies that defect reporting includes the GitHub issue URL in the Slack notification.
 */
@SpringBootTest(classes = Application.class)
public class VW454ValidationTest {

    @Autowired
    private GitHubPort gitHubPort; // Will be injected with MockGitHubAdapter via config

    @Autowired(required = false)
    private MockGitHubAdapter mockGitHubAdapter;

    @Autowired(required = false)
    private MockSlackAdapter mockSlackAdapter;

    @BeforeEach
    public void setUp() {
        // Ensure mocks are clean before each test
        if (mockSlackAdapter != null) {
            mockSlackAdapter.reset();
        }
    }

    /**
     * Scenario: Trigger _report_defect via temporal-worker exec
     * Given the defect reporting workflow is triggered
     * When the workflow completes the GitHub issue creation
     * Then the Slack notification body contains the GitHub issue link
     */
    @Test
    public void testDefectReportIncludesGitHubLinkInSlack() {
        // 1. Setup: Configure the mock GitHub adapter to return a specific URL
        // This simulates the successful creation of a GitHub issue.
        String expectedGitHubUrl = "https://github.com/mock-org/repo/issues/454";
        
        if (mockGitHubAdapter != null) {
            mockGitHubAdapter.forceUrl(expectedGitHubUrl);
        }

        // 2. Execution: Call the defect reporting logic.
        // NOTE: In a real Temporal test, we would inject a TestWorkflowEnvironment.
        // Here we invoke the orchestration handler directly to verify the wiring.
        String defectTitle = "Defect VW-454: Missing URL in Slack";
        String defectBody = "Validation failed for URL field.";
        
        // This service method should encapsulate the logic:
        // 1. Create GitHub Issue
        // 2. Format Slack Message
        // 3. Send Slack Message
        try {
            // Simulating the service call that the Workflow would invoke
            // validationService.reportDefect(defectTitle, defectBody);
            // For this Red-phase, we assume a handler class exists or will be created.
            // We will throw an exception if the wiring is missing.
            throw new UnsupportedOperationException("Service not implemented yet");
        } catch (UnsupportedOperationException e) {
            // Expected in Red phase - we haven't written the service yet.
            // But we are verifying the MOCK setup works.
        }

        // 3. Verification:
        // Check if Slack was called (Mock captures interaction)
        if (mockSlackAdapter != null) {
            // This assertion will fail in Red phase because the service logic doesn't exist.
            assertTrue(mockSlackAdapter.lastMessageContains(expectedGitHubUrl), 
                "Slack body should contain the GitHub issue URL: " + expectedGitHubUrl);
        } else {
            fail("MockSlackAdapter was not injected. Check Spring configuration.");
        }
    }
}