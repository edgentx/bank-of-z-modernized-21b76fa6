package com.example.domain.defect;

import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryIssueTrackerPort;
import com.example.mocks.InMemorySlackNotificationPort;
import com.example.ports.IssueTrackerPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1 / VW-454.
 * Validates that when a defect is reported, the resulting Slack notification
 * contains the clickable link to the GitHub issue.
 *
 * Uses the standard Repository/Port pattern to mock external dependencies.
 */
class SlackBodyContainsIssueLinkTest {

    // SUT (System Under Test) components
    private DefectAggregate aggregate;
    private InMemorySlackNotificationPort mockSlack;
    private InMemoryIssueTrackerPort mockTracker;

    @BeforeEach
    void setUp() {
        // Initialize mock adapters
        mockSlack = new InMemorySlackNotificationPort();
        mockTracker = new InMemoryIssueTrackerPort();

        // Inject dependencies via the Aggregate (or a Handler, but aggregate works for state machine)
        // Note: In this architecture, the Aggregate takes ports to trigger side-effects.
        String defectId = "VW-454";
        aggregate = new DefectAggregate(defectId, mockSlack, mockTracker);
    }

    @Test
    void shouldContainGitHubLinkInSlackBody_WhenDefectIsReported() {
        // ARRANGE
        // Configure the mock tracker to return a valid URL for the issue
        String expectedUrl = "https://github.com/bank-of-z/legacy-modernization/issues/454";
        mockTracker.setUrlForId("VW-454", expectedUrl);

        String channel = "#vforce360-issues";
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454",
            "Validating VW-454 — GitHub URL in Slack body",
            "Severity: LOW",
            "VForce360 PM diagnostic"
        );

        // ACT
        // Execute the command. The aggregate should publish an event AND trigger the port.
        var events = aggregate.execute(cmd);

        // ASSERT
        // 1. Verify the domain event was created
        assertFalse(events.isEmpty(), "Should generate a DefectReportedEvent");
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        // 2. Verify the Interaction: Slack port was called
        assertTrue(mockSlack.wasCalled(), "Slack notification should have been triggered");

        // 3. Verify the Constraint: The Slack body contains the GitHub URL
        String actualBody = mockSlack.getCapturedBody();
        assertNotNull(actualBody, "Slack body should not be null");
        
        // CRITICAL ASSERTION: The body must include the URL retrieved from the tracker
        assertTrue(
            actualBody.contains(expectedUrl),
            "Slack body must contain the valid GitHub issue link: " + expectedUrl + "\nActual Body: " + actualBody
        );
        
        // 4. Verify the message structure (basic formatting check)
        assertTrue(actualBody.contains("<"), "Slack link formatting should use <url> syntax");
        assertTrue(actualBody.contains(">"), "Slack link formatting should use <url> syntax");
    }

    @Test
    void shouldHandleMissingUrlGracefully() {
        // ARRANGE
        // Configure tracker to return empty (issue not found)
        mockTracker.setUrlForId("VW-999", null); // Not found

        DefectAggregate unknownAggregate = new DefectAggregate("VW-999", mockSlack, mockTracker);
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-999", "Unknown Issue", "Desc", "Test"
        );

        // ACT
        unknownAggregate.execute(cmd);

        // ASSERT
        String actualBody = mockSlack.getCapturedBody();
        assertNotNull(actualBody);
        
        // If no URL exists, we expect a fallback text or just the ID, but NOT a broken link
        assertFalse(actualBody.contains("https://"), "Should not contain a link if issue is not found");
        assertTrue(actualBody.contains("VW-999"), "Should contain the Issue ID");
    }
}
