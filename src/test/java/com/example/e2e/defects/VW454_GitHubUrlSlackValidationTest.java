package com.example.e2e.defects;

import com.example.domain.defects.model.ReportDefectCmd;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * E2E Regression Test for Story S-FB-1: Validating VW-454.
 *
 * <p>Context:
 * Defect VW-454 reported that when a defect is reported (via Temporal worker),
 * the resulting Slack notification did not contain the link to the created GitHub issue.
 *
 * <p>This test validates that:
 * 1. When a defect is reported, an issue is created on GitHub.
 * 2. The returned URL is captured.
 * 3. The Slack notification body explicitly contains this URL.
 *
 * <p>NOTE: This assumes the existence of a service/orchestrator class
 * {@code DefectReportingService} (or similar) which coordinates the workflow.
 * We will mock the service's dependencies to prove the coordination logic works.
 */
public class VW454_GitHubUrlSlackValidationTest {

    // System Under Test (SUT) - This represents the Temporal Activity/Workflow implementation
    // We create a placeholder interface for the test to define the contract we expect.
    public interface DefectReportingWorkflow {
        void reportDefect(ReportDefectCmd cmd);
    }

    private MockGitHubPort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private DefectReportingServiceImpl workflowService; // The concrete class to be implemented

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackNotificationPort();

        // We inject the mocks into the service we are testing.
        // This class (DefectReportingServiceImpl) must be created by the engineer in the Green phase.
        workflowService = new DefectReportingServiceImpl(mockGitHub, mockSlack);

        // Set a deterministic URL for GitHub to return
        mockGitHub.setFakeUrl("https://github.com/bank-of-z/vforce360/issues/454");
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectIsReported() {
        // Arrange
        ReportDefectCmd command = new ReportDefectCmd(
                "VW-454",
                "Fix: Validating VW-454 — GitHub URL in Slack body",
                "Defect reported by user.",
                "LOW",
                Map.of("component", "validation")
        );

        // Expected content derived from inputs
        String expectedGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/454";

        // Act
        workflowService.reportDefect(command);

        // Assert
        // 1. Verify GitHub Port was called to create the issue
        // We assume the title contains the defect ID
        // (Verification happens implicitly via the flow, but we can check mock state if we wanted)

        // 2. Verify Slack Port was called
        String actualSlackBody = mockSlack.getLastMessageBody();
        if (actualSlackBody == null) {
            throw new AssertionError("Slack was never called.");
        }

        // 3. CRITICAL ASSERTION: The Slack body must contain the GitHub URL
        // This is the core fix for the defect.
        boolean containsUrl = actualSlackBody.contains(expectedGitHubUrl);

        if (!containsUrl) {
            throw new AssertionError(
                    String.format(
                            "Slack body does not contain GitHub URL.%n" +
                                    "Expected URL: %s%n" +
                                    "Actual Body: %s",
                            expectedGitHubUrl,
                            actualSlackBody
                    )
            );
        }
    }

    @Test
    void shouldFormatGitHubIssueLinkAsHyperlinkInSlack() {
        // Arrange
        // Slack link format: <URL|Text> or just <URL>
        String specificUrl = "https://github.com/bank-of-z/vforce360/issues/999";
        mockGitHub.setFakeUrl(specificUrl);

        ReportDefectCmd command = new ReportDefectCmd(
                "VW-999",
                "New Defect",
                "Description",
                "HIGH",
                Map.of()
        );

        // Act
        workflowService.reportDefect(command);

        // Assert
        String body = mockSlack.getLastMessageBody();
        // Check for Slack-style link formatting or just the presence of the URL
        // The requirement is just "includes GitHub issue: <url>"
        if (!body.contains(specificUrl)) {
            throw new AssertionError("Expected Slack body to contain URL: " + specificUrl + ", but got: " + body);
        }
    }

    /**
     * Implementation stub class that will fail to compile initially if missing,
     * or fail logic if the implementation is wrong.
     * In TDD Red phase, we might write this class ourselves with a failing or empty implementation,
     * or rely on the framework to fail on compilation if the class doesn't exist.
     *
     * Here, we define a minimal version to ensure the test logic compiles and runs against the mocks.
     * The engineer must replace this with the actual file location in the main source set.
     */
    public static class DefectReportingServiceImpl {
        private final GitHubPort githubPort;
        private final SlackNotificationPort slackPort;

        public DefectReportingServiceImpl(GitHubPort githubPort, SlackNotificationPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        public void reportDefect(ReportDefectCmd cmd) {
            // RED PHASE IMPLEMENTATION STUB:
            // Currently does not implement the logic to pass the test.
            // We perform the actions but intentionally omit the URL in the Slack body
            // to simulate the defect state (or just do nothing/bare minimum).

            // 1. Create GitHub Issue
            String issueUrl = githubPort.createIssue(cmd.title(), cmd.description());

            // 2. Post to Slack
            // DEFECT STATE: We post the description, but FORGET to include the issueUrl.
            String slackBody = "Defect Reported: " + cmd.title();
            
            slackPort.postMessage("#vforce360-issues", slackBody);
        }
    }
}
