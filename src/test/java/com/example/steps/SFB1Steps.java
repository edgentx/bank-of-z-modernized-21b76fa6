package com.example.steps;

import com.example.mocks.MockSlackClient;
import com.example.mocks.MockTemporalWorker;
import com.example.ports.SlackClientPort;
import com.example.ports.TemporalReportDefectPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Scenario: Verifying that the Slack body contains the GitHub Issue URL.
 *
 * Given the 'report_defect' workflow is triggered
 * When the defect processing completes
 * Then the Slack message body should contain a valid GitHub Issue URL.
 */
public class SFB1Steps {

    // We assume a Spring ApplicationContext would wire this in production.
    // For the Red Phase, we mock the dependencies manually or via a test configuration.
    private MockSlackClient mockSlackClient;
    private TemporalReportDefectPort reportDefectWorkflow;
    private MockTemporalWorker temporalWorker;

    @BeforeEach
    public void setUp() {
        // 1. Initialize Mocks
        mockSlackClient = new MockSlackClient();

        // 2. Initialize the System Under Test (SUT)
        // Note: In the Red phase, 'ReportDefectWorkflowImpl' likely doesn't exist or is empty.
        // We rely on the Port interface.
        // If the implementation class exists, it would be injected here.
        // For now, we define the test expecting the behavior.
        
        // Pseudo-binding to demonstrate the dependency chain
        // This represents the class we EXPECT the engineer to write.
        reportDefectWorkflow = new ReportDefectWorkflowImpl(mockSlackClient);
        
        temporalWorker = new MockTemporalWorker(reportDefectWorkflow);
    }

    @Test
    public void testSlackBodyContainsGitHubLink() {
        // Arrange
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454";
        String description = "GitHub URL missing in Slack body";

        // Act
        temporalWorker.executeReportDefect(defectId, title, description);

        // Assert
        assertEquals(1, mockSlackClient.getMessages().size(), "Slack should have received one message");

        MockSlackClient.Message msg = mockSlackClient.getLatestMessage();
        assertEquals("#vforce360-issues", msg.channel, "Message should go to the issues channel");

        // The critical assertion for the defect fix
        // We look for 'github.com' or '<http...' which is Slack's link format
        assertTrue(
            msg.body.contains("github.com") || msg.body.contains("<http"),
            "Slack body must contain the GitHub issue URL. Body was: " + msg.body
        );
    }

    @Test
    public void testSlackBodyContentStructure() {
        // Additional sanity check for the message structure
        String defectId = "VW-455";
        temporalWorker.executeReportDefect(defectId, "Test Title", "Test Desc");

        MockSlackClient.Message msg = mockSlackClient.getLatestMessage();
        
        assertNotNull(msg.body, "Body should not be null");
        assertFalse(msg.body.isBlank(), "Body should not be empty");
        // Verify it mentions the defect ID
        assertTrue(msg.body.contains(defectId), "Body should reference the Defect ID");
    }

    /**
     * STUB CLASS.
     * This class represents the missing implementation.
     * In the TDD Red phase, this class might not exist yet, or it exists but does nothing.
     * We include it here to allow the test to compile (Red -> Refactor -> Green).
     * The 'Actual Behavior' in the defect report implies this logic is broken or missing.
     */
    private static class ReportDefectWorkflowImpl implements TemporalReportDefectPort {
        private final SlackClientPort slackClient;

        public ReportDefectWorkflowImpl(SlackClientPort slackClient) {
            this.slackClient = slackClient;
        }

        @Override
        public void reportDefect(String defectId, String title, String description) {
            // INTENTIONAL FLAW / MISSING IMPLEMENTATION
            // The defect states the link is missing.
            // To ensure the test FAILS initially (if we were simulating the bug),
            // we would post a message without the URL.
            // However, since this is the 'Fix' story, we assume the code is currently broken.
            // If the code currently does this:
            slackClient.postMessage("#vforce360-issues", "Defect reported: " + defectId + " - " + title);
            // The test 'testSlackBodyContainsGitHubLink' will fail because the URL is missing.
            
            // Note: The prompt asks for "tests for this story". 
            // If the implementation was already correct, the test would pass immediately (False Green).
            // Assuming the defect is active, this stub reflects the broken state or empty state.
        }
    }
}
