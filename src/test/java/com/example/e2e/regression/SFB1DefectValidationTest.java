package com.example.e2e.regression;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackNotificationPort;
import com.example.mocks.MockVForce360Port;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * 
 * Context:
 * - Trigger _report_defect via temporal-worker exec
 * - Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior:
 * - Slack body includes GitHub issue: <url>
 * 
 * Actual Behavior (Prior to fix):
 * - About to find out — checking #vforce360-issues for the link line
 */
class SFB1DefectValidationTest {

    private MockVForce360Port mockVForce360;
    private MockSlackNotificationPort mockSlack;
    private ValidationAggregate aggregate;

    private static final String TEST_DEFECT_ID = "VW-454";
    private static final String TEST_TITLE = "Validating VW-454 — GitHub URL in Slack body";

    @BeforeEach
    void setUp() {
        mockVForce360 = new MockVForce360Port();
        mockSlack = new MockSlackNotificationPort();
        
        // Set up the mock to return a specific URL
        mockVForce360.setMockReturnUrl("https://github.com/bank-of-z/issues/454");

        // Initialize the aggregate with mocks (Dependency Injection)
        aggregate = new ValidationAggregate(TEST_DEFECT_ID, mockVForce360, mockSlack);
    }

    @Test
    void testReportDefect_generatesSlackBodyContainingGitHubUrl() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(TEST_DEFECT_ID, TEST_TITLE);

        // Act
        List<DefectReportedEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Processing defect report should produce an event");
        
        // Verify VForce360 was called
        assertTrue(mockVForce360.wasCalledWith(TEST_TITLE), "VForce360 port should be called with the defect title");
        assertEquals(1, mockVForce360.getCallCount(), "VForce360 reportDefect should be called exactly once");

        // Verify Slack was called
        assertEquals(1, mockSlack.getCallCount(), "Slack notification should be sent exactly once");
        
        // CRITICAL ASSERTION: Verify the Slack body contains the expected GitHub URL format
        // Acceptance Criteria: "Slack body includes GitHub issue: <url>"
        boolean slackBodyContainsUrl = mockSlack.containsMessage("https://github.com/bank-of-z/issues/454");
        assertTrue(slackBodyContainsUrl, "Slack body MUST contain the GitHub issue URL returned by VForce360");

        // Verify the exact format expected by the user story
        boolean slackBodyContainsLabel = mockSlack.containsMessage("GitHub issue:");
        assertTrue(slackBodyContainsLabel, "Slack body MUST contain the label 'GitHub issue:'");
    }

    @Test
    void testReportDefect_failsIfVForce360ReturnsNull() {
        // Arrange
        mockVForce360.setShouldFail(true); // Simulate VForce360 returning null
        ReportDefectCmd cmd = new ReportDefectCmd(TEST_DEFECT_ID, TEST_TITLE);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Failed to generate GitHub URL"));
        
        // Verify Slack was NOT called if URL generation failed
        assertEquals(0, mockSlack.getCallCount(), "Slack should NOT be called if VForce360 fails to generate URL");
    }

    @Test
    void testReportDefect_eventContainsCorrectPayload() {
        // Arrange
        String expectedUrl = "https://github.com/bank-of-z/issues/999";
        mockVForce360.setMockReturnUrl(expectedUrl);
        ReportDefectCmd cmd = new ReportDefectCmd("DEFECT-99", "Critical Failure");

        // Act
        aggregate.execute(cmd);
        
        // In a real repository scenario, we'd fetch from the event store. 
        // Here we check the uncommitted events on the aggregate.
        List<DefectReportedEvent> events = aggregate.uncommittedEvents();
        
        assertFalse(events.isEmpty());
        DefectReportedEvent event = events.get(0);
        
        assertEquals("DEFECT-99", event.aggregateId());
        assertEquals("Critical Failure", event.title());
        assertEquals(expectedUrl, event.githubUrl());
        assertNotNull(event.occurredAt());
    }
}
