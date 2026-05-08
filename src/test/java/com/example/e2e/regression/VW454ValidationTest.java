package com.example.e2e.regression;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.service.VForce360Workflow;
import com.example.mocks.MockGitHubClient;
import com.example.mocks.MockSlackNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for S-FB-1.
 * Verifies that when a defect is reported, the resulting Slack body
 * contains the valid GitHub issue URL.
 */
public class VW454ValidationTest {

    private VForce360Workflow workflow;
    private MockSlackNotifier mockSlack;
    private MockGitHubClient mockGitHub;

    @BeforeEach
    public void setup() {
        mockSlack = new MockSlackNotifier();
        mockGitHub = new MockGitHubClient();
        // Injecting mocks directly into the workflow for testing
        workflow = new VForce360Workflow(mockSlack, mockGitHub);
    }

    @Test
    public void testSlackBodyContainsGitHubIssueLink() {
        // Arrange
        DefectAggregate defect = new DefectAggregate("DEFECT-101");
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        mockGitHub.setMockUrl(expectedUrl);

        // Act
        workflow.handleDefectReport(defect);

        // Assert
        // 1. Verify Slack was called
        assertTrue(mockSlack.messages.size() > 0, "Slack notifier should have received a message");

        // 2. Verify content contains URL
        String slackMessage = mockSlack.getLastMessage();
        assertNotNull(slackMessage, "Slack message should not be null");
        assertTrue(slackMessage.contains(expectedUrl), "Slack body must contain the GitHub issue URL: " + expectedUrl);
    }
}
