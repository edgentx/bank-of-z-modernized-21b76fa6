package com.example.e2e.regression;

import com.example.domain.reconciliation.model.ReportDefectCmd;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for Story S-FB-1.
 * 
 * Verifies that triggering the defect reporting workflow results in a Slack message
 * containing the correctly formatted GitHub issue URL.
 */
@SpringBootTest
public class S_FB_1_DefectLinkValidationTest {

    @Autowired
    private ReconciliationBatchRepository reconciliationBatchRepository;

    // We use the mock port to capture outputs without sending real Slack messages
    @Autowired
    private SlackNotificationPort slackNotificationPort;

    // Helper to cast the bean to the mock implementation for assertions
    private MockSlackNotificationPort getMockPort() {
        if (slackNotificationPort instanceof MockSlackNotificationPort mock) {
            return mock;
        }
        throw new IllegalStateException("Expected MockSlackNotificationPort to be injected");
    }

    @BeforeEach
    public void setUp() {
        getMockPort().reset();
    }

    @Test
    public void testReportDefect_generatesSlackMessageContainingGitHubUrl() {
        // Arrange
        // ID: VW-454
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String defectId = "VW-454";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            projectId,
            "Validating GitHub URL in Slack body",
            "LOW",
            "validation"
        );

        // Act
        // In a real Temporal setup, we would trigger the workflow.
        // For this unit/regression test, we assume a service/handler orchestrates the logic.
        // We will trigger the logic via the Aggregate or a Service bean.
        // Assuming a handler exists that processes this command.
        // For now, we simulate the expected workflow logic execution via the Aggregate.
        
        // NOTE: The 'execute' method logic is what we are testing.
        // If we use the Aggregate directly:
        var aggregate = reconciliationBatchRepository.findById("recon-1").orElseThrow();
        var events = aggregate.execute(cmd);

        // Assert
        // 1. Verify Event Created
        assertFalse(events.isEmpty(), "Defect reporting should produce an event");

        // 2. Verify Slack Interaction (Mock verification)
        MockSlackNotificationPort mockPort = getMockPort();
        List<MockSlackNotificationPort.SlackMessage> messages = mockPort.sentMessages;
        
        assertEquals(1, messages.size(), "Exactly one Slack message should be sent");
        
        MockSlackNotificationPort.SlackMessage msg = messages.get(0);
        assertEquals("#vforce360-issues", msg.channel(), "Message should go to the correct channel");

        // 3. Verify Critical Content: GitHub URL Format
        // Expected format: https://github.com/tech-debt/project/issues/454
        String body = msg.body();
        assertTrue(
            body.contains("https://github.com/tech-debt/project/issues/"),
            "Slack body must contain the GitHub issue URL prefix"
        );
        
        // Specific for VW-454
        assertTrue(
            body.contains("/454"),
            "Slack body must contain the specific issue ID derived from the defect ID"
        );
    }
}