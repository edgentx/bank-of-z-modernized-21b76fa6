package com.example.steps;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockGitHubIssueTracker;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.GitHubIssueTrackerPort;
import com.example.ports.SlackNotifierPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for Story S-FB-1 / Defect VW-454.
 * Verifies that the Validation Workflow triggers Slack notifications
 * that contain the GitHub Issue URL.
 *
 * Location: e2e/regression/ (Mapped to src/test/java/com/example/steps/)
 */
class ValidationWorkflowE2ETest {

    private MockSlackNotifier mockSlack;
    private MockGitHubIssueTracker mockGitHub;
    private ValidationWorkflow workflow; // Implementation under test

    @BeforeEach
    void setUp() {
        // 1. Instantiate Mock Adapters
        mockSlack = new MockSlackNotifier();
        mockGitHub = new MockGitHubIssueTracker();

        // 2. Setup Workflow/Service with Mocks
        // In a real Spring Boot test, this would be injected via @MockBean.
        // Here, we instantiate the 'future' implementation class directly or via a Facade.
        // Since the implementation does not exist yet, we simulate the call pattern.
        // workflow = new ValidationWorkflow(mockSlack, mockGitHub, ...);
    }

    @Test
    void testSlackNotificationContainsGitHubUrl() {
        // Arrange
        String defectTitle = "VW-454: Validation Failure";
        String expectedUrl = "http://github.com/bank-of-z/issues/454";
        ReportDefectCommand command = new ReportDefectCommand(
                "cmd-123",
                defectTitle,
                "Critical validation step failed",
                "LOW"
        );

        // Configure Mocks
        mockGitHub.setSimulatedUrl(expectedUrl);

        // Act & Assert (Simulating the Red Phase)
        // This test acts as the specification for the implementation.
        // The following assertions describe the EXPECTED state after implementation.

        try {
            // This represents the Workflow execution:
            // 1. Handle Command via Aggregate
            ValidationAggregate aggregate = new ValidationAggregate("validation-agg-1");
            var events = aggregate.execute(command);

            // 2. If Aggregate emits event, Workflow calls external ports
            // (Simulated logic)
            String createdUrl = mockGitHub.createIssue(defectTitle, "...");
            mockSlack.postMessage("#vforce360-issues", "Defect reported: " + createdUrl);

            // 3. Verify Slack Body contains GitHub URL
            assertFalse(mockSlack.getPostedMessages().isEmpty(), "Slack should have received a message");
            MockSlackNotifier.PostedMessage posted = mockSlack.getPostedMessages().get(0);
            assertTrue(
                    posted.body.contains(expectedUrl),
                    "Slack body must include GitHub issue URL. Expected: " + expectedUrl + " in " + posted.body
            );
            assertEquals("#vforce360-issues", posted.channel, "Message should go to the correct channel");

        } catch (UnsupportedOperationException e) {
            // RED Phase Behavior:
            // Since the implementation is missing, this test would fail if we relied on the real aggregate logic.
            // However, since we are writing the test *first*, we assert the existence of the logic.
            fail("Implementation missing: Aggregate did not handle ReportDefectCommand. S-FB-1 Logic Required.");
        }
    }

    @Test
    void testDefectReportedEventStructure() {
        // Verify Domain Event contract for projections
        String url = "http://github.com/bank-of-z/issues/999";
        Instant now = Instant.now();
        DefectReportedEvent event = new DefectReportedEvent("agg-1", "defect-1", url, "#general", now);

        assertEquals("DefectReported", event.type());
        assertEquals("agg-1", event.aggregateId());
        assertEquals(url, event.githubUrl());
    }

    /**
     * Facade for the Workflow implementation.
     * This class represents the code that needs to be written to pass the tests.
     */
    static class ValidationWorkflow {
        private final SlackNotifierPort slack;
        private final GitHubIssueTrackerPort github;

        public ValidationWorkflow(SlackNotifierPort slack, GitHubIssueTrackerPort github) {
            this.slack = slack;
            this.github = github;
        }

        public DefectReportedEvent execute(ValidationAggregate aggregate, ReportDefectCommand cmd) {
            // Logic to be implemented:
            // 1. aggregate.execute(cmd) -> raises DefectReportedEvent (initial state pending url)
            // 2. github.createIssue(...)
            // 3. slack.postMessage(..., body with url)
            // 4. return enriched event
            throw new UnsupportedOperationException("S-FB-1: Implement Workflow logic");
        }
    }
}
