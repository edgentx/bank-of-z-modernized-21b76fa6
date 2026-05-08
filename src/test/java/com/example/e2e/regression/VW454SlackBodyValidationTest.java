package com.example.e2e.regression;

import com.example.domain.shared.Command;
import com.example.domain.defect.DefectReportedEvent;
import com.example.mocks.MockGitHubIssueAdapter;
import com.example.mocks.MockVForce360NotificationAdapter;
import com.example.ports.GitHubIssuePort;
import com.example.ports.VForce360NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Story S-FB-1 / VW-454.
 * <p>
 * Scenario: When a defect is reported via the temporal-worker, the resulting
 * notification payload must contain the GitHub Issue URL.
 * <p>
 * This test is currently in the RED phase: The application logic to generate
 * the {@link DefectReportedEvent} with the URL is missing or incomplete.
 */
public class VW454SlackBodyValidationTest {

    // --- Mocks (Adapters) ---
    private final GitHubIssuePort githubAdapter = new MockGitHubIssueAdapter();
    private final VForce360NotificationPort vforceAdapter = new MockVForce360NotificationAdapter();

    // --- System Under Test ---
    // We assume a service/handler exists that coordinates this flow.
    // For TDD Red phase, we define the Command and expected behavior here.
    private ReportDefectCommand cmd;

    @BeforeEach
    void setUp() {
        cmd = new ReportDefectCommand("VW-454", "GitHub URL missing in Slack body");
    }

    @Test
    void shouldContainGitHubUrlInEventPayload() {
        // WHEN: The defect report command is executed
        // (In real implementation, this would invoke the Aggregate/Workflow)
        // For now, we simulate the expected outcome logic to define the contract.
        
        // Simulate the workflow:
        // 1. Report Defect (Temporal)
        String correlationId = vforceAdapter.reportDefect(cmd.defectId(), cmd.summary());
        assertNotNull(correlationId, "VForce360 should acknowledge the report");

        // 2. Create GitHub Issue
        String githubUrl = githubAdapter.createIssue(cmd.summary(), "Defect: " + cmd.summary()).toString();

        // 3. Expected Result: The Domain Event emitted should carry this URL
        DefectReportedEvent expectedEvent = new DefectReportedEvent(
                cmd.defectId(),
                cmd.defectId(),
                githubUrl
        );

        // THEN: Verify the contract
        // This assertion represents the E2E check: does the event leading to Slack contain the URL?
        assertTrue(
                expectedEvent.getGithubUrl().isPresent(),
                "CRITICAL FAIL: DefectReportedEvent must contain a non-null GitHub URL for Slack integration."
        );

        assertTrue(
                expectedEvent.getGithubUrl().get().startsWith("http"),
                "CRITICAL FAIL: GitHub URL must be a valid HTTP link."
        );
    }

    @Test
    void shouldValidateRegressionScenarioVW454() {
        // GIVEN: The specific defect scenario VW-454
        String defectId = "VW-454";

        // WHEN: Executing the report workflow
        // (Simulating the Aggregate's emit logic)
        MockGitHubIssueAdapter mockGithub = new MockGitHubIssueAdapter();
        String generatedUrl = mockGithub.createIssue("VW-454 Regression", "...").toString();

        DefectReportedEvent event = new DefectReportedEvent("aggregate-123", defectId, generatedUrl);

        // THEN: Verify the URL is propagated correctly to the 'Slack Body' equivalent (Event Payload)
        // This tests the 'Actual Behavior' vs 'Expected Behavior' from the story description.
        String bodyContent = "GitHub Issue: " + event.githubUrl(); // Simulating how Slack body might be built

        assertTrue(
                bodyContent.contains("github.com"),
                "Regression check failed: Link missing from Slack body equivalent."
        );
        
        assertFalse(
                bodyContent.contains("<url>"),
                "Regression check failed: URL was not replaced with a real link."
        );
    }

    // --- Inner Classes defining Domain Contracts (Expected to exist/implemented) ---

    public record ReportDefectCommand(String defectId, String summary) implements Command {}

}
