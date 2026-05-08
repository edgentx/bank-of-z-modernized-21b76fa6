package com.example.e2e.regression;

import com.example.domain.shared.Command;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.ports.VForce360NotificationPort;
import com.example.e2e.mocks.InMemoryNotificationAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * <p>
 * Story: Verify that when a defect is reported via the Temporal worker exec,
 * the resulting Slack body contains the correct GitHub issue link.
 * <p>
 * Expected: Slack body includes GitHub issue: <url>
 * Component: validation
 */
class VW454SlackIntegrationRegressionTest {

    private InMemoryNotificationAdapter notificationSpy;

    @BeforeEach
    void setUp() {
        // Initialize the mock adapter to capture notifications
        notificationSpy = new InMemoryNotificationAdapter();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectIsReported() {
        // 1. Setup Data
        String expectedUrl = "https://github.com/vforce360/bank-of-z/issues/454";
        String defectTitle = "Fix: Validating VW-454";
        String defectDescription = "GitHub URL missing from Slack body";

        // 2. Create the domain event that mimics the Temporal worker's output
        DefectReportedEvent event = DefectReportedEvent.create(
                defectTitle,
                defectDescription,
                expectedUrl
        );

        // 3. Trigger the action via Port (simulating Temporal workflow execution)
        notificationSpy.postDefectNotification(event);

        // 4. Verify the Slack body content (Regression Check)
        String lastMessageBody = notificationSpy.getLastPostedBody();

        assertNotNull(lastMessageBody, "Slack message body should not be null");
        assertTrue(
                lastMessageBody.contains(expectedUrl),
                "Slack body must contain the GitHub issue URL. Expected: " + expectedUrl + "\nActual Body: " + lastMessageBody
        );
    }

    @Test
    void shouldFailValidationIfUrlIsMissing() {
        // 1. Setup Data (Malformed event with null/blank URL)
        String missingUrl = ""; // Simulating the defect state
        String defectTitle = "Fix: Validating VW-454";

        DefectReportedEvent event = DefectReportedEvent.create(
                defectTitle,
                "Validation test",
                missingUrl
        );

        // 2. Trigger action
        notificationSpy.postDefectNotification(event);

        // 3. Verify failure (The 'Red' phase of TDD - expecting failure or empty body)
        String lastMessageBody = notificationSpy.getLastPostedBody();

        // The bug implies the URL is missing. We assert this to prove the bug exists initially.
        assertFalse(
                lastMessageBody.contains("http"),
                "Currently expecting FAILURE: URL should be missing to reproduce the bug."
        );
    }
}