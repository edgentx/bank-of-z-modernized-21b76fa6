package com.example.domain.vforce;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class covering VW-454: Validating GitHub URL in Slack body.
 * 
 * Context: The temporal-worker executes "_report_defect".
 * This logic is represented here by a simple handler class under test.
 * 
 * AC: The validation no longer exhibits the reported behavior (missing URL).
 * AC: Regression test added covering this scenario.
 */
public class VW454ValidationTest {

    private MockSlackNotificationPort slackPort;
    private MockGitHubPort gitHubPort;
    private DefectReporter defectReporter; // The class we are effectively testing/stubbing

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
        gitHubPort = new MockGitHubPort();
        
        // Initialize the System Under Test (SUT) with its dependencies.
        // Note: In the 'Red' phase, DefectReporter might not exist or do nothing.
        // We assume the interface/shape based on the requirements.
        defectReporter = new DefectReporter(slackPort, gitHubPort);
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Given
        String defectId = "VW-454";
        String slackChannel = "#vforce360-issues";
        String expectedUrl = "https://github.com/dummy-repo/issues/VW-454";
        
        // When
        // The temporal worker triggers this logic
        defectReporter.reportDefect(defectId, slackChannel);

        // Then
        assertEquals(1, slackPort.getPostedMessages().size(), "Slack should receive one message");
        
        MockSlackNotificationPort.PostedMessage msg = slackPort.getPostedMessages().get(0);
        assertEquals(slackChannel, msg.channel, "Message should be sent to the correct channel");
        
        // The core validation: The body MUST contain the GitHub URL
        assertTrue(msg.body.contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL. Received: " + msg.body);
    }

    @Test
    void testReportDefect_ShouldHandleMissingUrlGracefully() {
        // Given
        gitHubPort.setShouldReturnEmpty(true); // Simulate GitHub failure/not found
        String defectId = "VW-454";
        String slackChannel = "#vforce360-issues";

        // When
        defectReporter.reportDefect(defectId, slackChannel);

        // Then
        assertEquals(1, slackPort.getPostedMessages().size());
        MockSlackNotificationPort.PostedMessage msg = slackPort.getPostedMessages().get(0);
        
        // If the URL is missing, the system should probably indicate that, or fail safely.
        // Based on the "Actual Behavior" in the story (checking for the link line), 
        // presence of the link is the happy path. 
        // This test ensures we don't crash if GitHub is down, but the Link won't be there.
        assertFalse(msg.body.contains("https://github.com"), "Body should not contain a URL if GitHub returned empty");
    }
}
