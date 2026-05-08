package com.example.e2e.regression;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression test for VW-454.
 * Validates that when a defect is reported (triggered via temporal-worker exec),
 * the resulting Slack notification body contains the GitHub issue URL.
 */
public class VW454SlackUrlValidationTest {

    private MockSlackNotificationPort mockSlack;
    private MockGitHubPort mockGitHub;
    private DefectReportWorkflow workflow; // This class represents the SUT (System Under Test)

    @BeforeEach
    public void setUp() {
        mockSlack = new MockSlackNotificationPort();
        mockGitHub = new MockGitHubPort();
        // We inject the mocks into the Workflow/Service.
        // In a real Spring Boot test, we might use @MockBean, but this is plain Java TDD.
        workflow = new DefectReportWorkflow(mockGitHub, mockSlack);
    }

    @Test
    public void testReportDefect_ShouldContainGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454: Validation failure";
        String defectDescription = "Validation is not correctly checking the GitHub URL";
        String expectedGitHubUrl = "https://github.com/test/repo/issues/1";

        // Act
        // This simulates the Temporal execution of _report_defect
        workflow.reportDefect(defectTitle, defectDescription);

        // Assert
        // 1. Verify Slack was called
        assertEquals(1, mockSlack.getSentMessages().size(), "Slack should have been called exactly once");

        // 2. Verify GitHub was called (Implicitly mocked, but good to know)
        // Note: MockGitHubPort automatically returns the expected URL

        // 3. Verify the Slack Body contains the GitHub URL
        String actualSlackBody = mockSlack.getSentMessages().get(0);
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body should contain the GitHub issue URL. Expected: [" + expectedGitHubUrl + "] in: [" + actualSlackBody + "]"
        );
    }

    @Test
    public void testReportDefect_GitHubUrlFormat() {
        // Arrange
        String defectTitle = "VW-454: URL Format Check";
        String defectDescription = "Check URL formatting";

        // Act
        workflow.reportDefect(defectTitle, defectDescription);

        // Assert
        String actualSlackBody = mockSlack.getSentMessages().get(0);
        
        // Ensure the link is not null and not empty inside the body
        assertNotNull(actualSlackBody, "Slack body must not be null");
        assertFalse(actualSlackBody.isEmpty(), "Slack body must not be empty");
        
        // Basic check for a URL pattern (http/https)
        assertTrue(actualSlackBody.contains("http"), "Body should contain a valid URL protocol");
    }

    /**
     * SUT Placeholder: This class represents the workflow logic being tested.
     * In the Red phase, this class might not exist or will have a stub implementation.
     * We define it here to make the test compile and express the intended interface.
     */
    public static class DefectReportWorkflow {
        private final GitHubPort githubPort;
        private final SlackNotificationPort slackPort;

        public DefectReportWorkflow(GitHubPort githubPort, SlackNotificationPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        public void reportDefect(String title, String description) {
            // This is the logic we are testing.
            // Phase RED: Implementation is missing or wrong.
            
            // Correct Implementation (for developer reference later):
            // String url = githubPort.createIssue(title, description);
            // String slackBody = "Issue created: " + url;
            // slackPort.sendMessage(slackBody);
            
            // Current Implementation (Stub to ensure test fails initially):
            slackPort.sendMessage("Defect reported: " + title);
        }
    }
}
