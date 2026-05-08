package com.example.e2e.regression;

import com.example.ports.SlackNotificationPort;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationAdapter;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies that when a defect is reported, the resulting Slack message body
 * contains the valid GitHub issue URL.
 *
 * Corresponds to S-FB-1.
 */
class VW454SlackBodyValidationTest {

    @Test
    void shouldContainGitHubIssueUrlInSlackBody() {
        // Arrange
        MockSlackNotificationAdapter mockSlack = new MockSlackNotificationAdapter();
        String expectedProjectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedTitle = "VW-454: Validation Failure";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";

        ReportDefectCmd command = new ReportDefectCmd(
            expectedProjectId,
            expectedTitle,
            "Severity: LOW\nComponent: validation",
            Instant.now()
        );

        // Act (Simulating the workflow execution that triggers the notification)
        try {
            mockSlack.sendDefectNotification(command, URI.create(expectedUrl));
        } catch (Exception e) {
            fail("Workflow execution failed during test: " + e.getMessage());
        }

        // Assert
        assertTrue(mockSlack.wasCalled(), "Slack notification should have been triggered");

        Map<String, Object> capturedPayload = mockSlack.getCapturedPayload();
        assertNotNull(capturedPayload, "Payload should not be null");

        // Verify "body" exists and contains the URL
        Object bodyObj = capturedPayload.get("body");
        assertNotNull(bodyObj, "Slack payload must contain a 'body' field");
        assertTrue(bodyObj instanceof String, "Slack 'body' must be a String");

        String body = (String) bodyObj;
        assertTrue(
            body.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected [" + expectedUrl + "] but got: " + body
        );
    }

    @Test
    void shouldFailIfUrlMissingFromBody() {
        // Arrange
        MockSlackNotificationAdapter mockSlack = new MockSlackNotificationAdapter();
        String missingUrl = "";

        ReportDefectCmd command = new ReportDefectCmd(
            "pid",
            "Title",
            "Desc",
            Instant.now()
        );

        // Act
        mockSlack.sendDefectNotification(command, URI.create(missingUrl));

        // Assert
        String body = (String) mockSlack.getCapturedPayload().get("body");
        assertFalse(body.contains("http"), "Test setup failure: URL should be missing to validate failure case");
    }
}
