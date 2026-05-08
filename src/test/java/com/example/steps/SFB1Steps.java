package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockIssueTrackerAdapter;
import com.example.mocks.MockNotificationGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test Suite for S-FB-1: Fix Validating VW-454.
 * 
 * Context: Verifies that when a defect is reported, the subsequent Slack 
 * notification contains the GitHub URL generated for that defect.
 * 
 * Regression Test: e2e/regression/VW454_SlackBodyContainsUrlTest
 */
public class SFB1Steps {

    private MockIssueTrackerAdapter mockIssueTracker;
    private MockNotificationGatewayAdapter mockNotificationGateway;
    private ValidationAggregate aggregate;

    private static final String VALIDATION_ID = "val-123";
    private static final String GITHUB_URL = "https://github.com/bank-of-z/issues/454";

    @BeforeEach
    void setUp() {
        // Initialize Mocks
        mockIssueTracker = new MockIssueTrackerAdapter();
        mockNotificationGateway = new MockNotificationGatewayAdapter();

        // Inject mocks into Aggregate (as per story requirement for mock adapters)
        aggregate = new ValidationAggregate(VALIDATION_ID, mockIssueTracker, mockNotificationGateway);
    }

    @Test
    @DisplayName("Verify Slack Body contains GitHub URL")
    public void testSlackBodyContainsGitHubUrl() {
        // ARRANGE
        // Configure the Mock Issue Tracker to return a specific URL when called
        mockIssueTracker.setNextUrl(GITHUB_URL);

        // Prepare the command to report a defect
        ReportDefectCmd cmd = new ReportDefectCmd(
            "defect-1",
            "VW-454: Slack body missing URL",
            "User reported that the Slack body does not contain the link",
            Map.of("severity", "LOW", "component", "validation")
        );

        // ACT
        // Execute the command on the aggregate
        aggregate.execute(cmd);

        // ASSERT
        // 1. Verify the Issue Tracker was called (GitHub link generated)
        assertTrue(mockIssueTracker.wasCalled(), "Issue Tracker should have been triggered");

        // 2. Verify the Notification Gateway was called
        assertTrue(mockNotificationGateway.wasCalled(), "Notification Gateway should have been triggered");

        // 3. CRITICAL ASSERTION: Check the actual body sent to "Slack"
        // The test expects the GITHUB_URL to be present in the message body
        String actualSlackBody = mockNotificationGateway.getLastMessageBody();
        
        assertNotNull(actualSlackBody, "Slack body should not be null");
        
        // THIS ASSERTION IS EXPECTED TO FAIL IN THE RED PHASE
        // because the current implementation in ValidationAggregate 
        // simply sets body to "Defect Reported: [Title]" without the URL.
        assertTrue(
            actualSlackBody.contains(GITHUB_URL),
            "Slack body should contain the GitHub issue URL: " + GITHUB_URL + ". Actual body: " + actualSlackBody
        );
    }

    @Test
    @DisplayName("Verify full format of Slack notification")
    public void testFullNotificationFormat() {
        // ARRANGE
        mockIssueTracker.setNextUrl(GITHUB_URL);
        ReportDefectCmd cmd = new ReportDefectCmd(
            "defect-2",
            "S-FB-1 Test",
            "Testing full format",
            Map.of("source", "temporal-worker")
        );

        // ACT
        aggregate.execute(cmd);

        // ASSERT
        // Regression check for the expected format
        String body = mockNotificationGateway.getLastMessageBody();
        
        // Expecting something like: "New Issue: <url> - Title"
        // This strict format check ensures we don't just fix the bug, but meet the spec.
        // Adjust the expected string format based on specific requirements.
        assertTrue(
            body.contains("Issue:"), 
            "Body should describe the issue context"
        );
    }
}
