package com.example.e2e.regression;

import com.example.domain.shared.DomainEvent;
import com.example.domain.vforce360.DefectReportedEvent;
import com.example.domain.vforce360.ReportDefectCmd;
import com.example.domain.vforce360.VForce360Aggregate;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression test for VW-454.
 * Ensures that when a defect is reported, the Slack notification body
 * contains the GitHub issue URL.
 */
public class VW454ValidationTest {

    private MockGitHubPort mockGitHubPort;
    private MockSlackNotificationPort mockSlackPort;
    private VForce360Aggregate aggregate;

    @BeforeEach
    void setUp() {
        mockGitHubPort = new MockGitHubPort();
        mockSlackPort = new MockSlackNotificationPort();
        
        // Configure a predictable URL
        mockGitHubPort.setNextIssueUrl("https://github.com/example/project/issues/454");

        // Initialize the aggregate with ports
        aggregate = new VForce360Aggregate("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", mockGitHubPort, mockSlackPort);
    }

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedUrl = "https://github.com/example/project/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", "VW-454 Validation Failure", "LOW");

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        // 1. Verify events were generated
        assertFalse(events.isEmpty(), "Should generate a DefectReportedEvent");
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Event type mismatch");

        // 2. Verify Slack interaction (The VW-454 core check)
        assertEquals(1, mockSlackPort.messages.size(), "Should send one Slack message");
        
        MockSlackNotificationPort.Message msg = mockSlackPort.messages.get(0);
        assertEquals("#vforce360-issues", msg.channel(), "Wrong channel");
        
        // THE FIX: Check that the body includes the GitHub URL
        assertTrue(msg.body().contains(expectedUrl), 
            "Slack body must contain the GitHub issue URL. Was: " + msg.body());
    }
}
