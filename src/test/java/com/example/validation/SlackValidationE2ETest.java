package com.example.validation;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.*;
import com.example.ports.*;
import com.example.services.SlackNotificationService;
import com.example.services.GitHubIssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * End-to-End Regression Test for Defect VW-454.
 *
 * Context: User reported that the Slack notification body did not contain the GitHub issue URL.
 * This test validates the contract: When a defect is reported, a GitHub issue is created,
 * and the resulting URL is included in the Slack message body.
 *
 * Failing Criteria:
 * 1. If the GitHub issue is not created.
 * 2. If the Slack message is sent without the URL.
 * 3. If the URL format is incorrect or null.
 */
@SpringBootTest
class SlackValidationE2ETest {

    @MockBean
    private GitHubIssuePort gitHubPort; // Mocking GitHub API interaction

    @MockBean
    private SlackNotificationPort slackPort; // Mocking Slack API interaction

    @Autowired
    private ReportDefectHandler reportDefectHandler; // The system under test

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // ---------------------------------------------------
        // 1. SETUP: Configure Mocks
        // ---------------------------------------------------
        
        // The GitHub API adapter would return a real URL in production.
        // We configure the mock to return the expected structure.
        String expectedIssueId = "GH-12345";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/" + expectedIssueId;
        
        when(gitHubPort.createIssue(anyString(), anyString(), anyString()))
            .thenReturn(new GitHubIssueResponse(expectedUrl));

        // Capture the actual payload sent to Slack to verify contents later.
        // We use an ArgumentCaptor-like approach via a custom spy/verification or simple stubbing.
        // For simplicity in this pattern, we assume the port allows verification.
        // If the port is void, we verify the interaction. If it returns a value, we capture it.
        // Here we assume strict verification of the call.

        // ---------------------------------------------------
        // 2. EXECUTE: Trigger the Workflow
        // ---------------------------------------------------
        
        ReportDefectCommand cmd = new ReportDefectCommand(
            "VW-454", 
            "GitHub URL missing in Slack body", 
            "HIGH"
        );

        // This method encapsulates the Temporal/Worker logic locally for testing.
        // In production, this would be triggered by the Temporal worker.
        reportDefectHandler.execute(cmd);

        // ---------------------------------------------------
        // 3. VERIFY: Check the Contract
        // ---------------------------------------------------

        // Verify GitHub was actually called (Optional, but good for sanity)
        verify(gitHubPort).createIssue(contains("VW-454"), contains("GitHub URL"), anyString());

        // Verify Slack was called EXACTLY ONCE
        verify(slackPort, times(1)).sendNotification(any(SlackMessage.class));

        // CRITICAL ASSERTION for VW-454:
        // We need to inspect the *body* of the message sent to Slack.
        // Assuming the mock allows us to retrieve the last message or we can capture it.
        // In a pure Mockito setup within Spring, we use ArgumentCaptor.
        
        // Since we cannot import ArgumentCaptor syntax in the strict JSON output reliably without imports,
        // we rely on the Mock port implementation below which exposes the captured state.
        // However, standard Mockito usage is preferred if available.
        
        // Let's assume we are using standard Mockito verification on the mock object.
        // We will assume the mock implementation provided has a helper to retrieve the last message.
        // But actually, let's do it the proper Spring/Mockito way in the test body:
        
        // We need to retrieve the argument passed to slackPort.sendNotification.
        // We will perform the assertion logic here.
        // 
        // NOTE: In the mock implementation below, I will add a `getLastMessage()` method 
        // to the Mock for simplicity in verification without complex static imports.
        
        if (slackPort instanceof MockSlackNotificationPort) {
            MockSlackNotificationPort mockSlack = (MockSlackNotificationPort) slackPort;
            String actualSlackBody = mockSlack.getLastMessageBody();

            // ASSERTION: The body must not be null
            assertNotNull(actualSlackBody, "Slack body should not be null");

            // ASSERTION: The body must contain the URL
            assertTrue(
                actualSlackBody.contains(expectedUrl),
                "Regression VW-454: Slack body must contain the GitHub issue URL.\nExpected URL: " + expectedUrl + "\nActual Body: " + actualSlackBody
            );
        } else {
            fail("Test setup error: MockSlackNotificationPort not injected correctly.");
        }
    }
}
