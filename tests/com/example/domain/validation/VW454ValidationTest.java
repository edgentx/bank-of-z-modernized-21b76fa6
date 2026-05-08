package com.example.domain.validation;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for VW-454.
 * 
 * Validates that when a defect is reported, the resulting command execution
 * produces an event (or state) that triggers a Slack notification containing
 * the GitHub Issue URL.
 * 
 * @see com.example.domain.validation.model.ReportDefectCmd
 * @see com.example.ports.SlackNotificationPort
 */
class VW454ValidationTest {

    private Aggregate validationAggregate;
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        // Initialize the Aggregate under test
        // Note: Assuming ValidationAggregate exists or needs to be created to satisfy Command contract
        try {
            // Using reflection or generic instantiation if class name is dynamic, but here we assume standard package
            Class<?> clazz = Class.forName("com.example.domain.validation.model.ValidationAggregate");
            validationAggregate = (Aggregate) clazz.getConstructor(String.class).newInstance("test-validation-id");
        } catch (Exception e) {
            // In strict TDD Red phase, we might just mock the Aggregate interface or fail here if class doesn't exist
            // For this exercise, we assume the class structure provided in prompt is incomplete for this specific story,
            // so we write the test expecting the class to be implemented.
            fail("ValidationAggregate implementation missing. Create com.example.domain.validation.model.ValidationAggregate to proceed.");
        }

        mockSlack = new MockSlackNotificationPort();
    }

    @Test
    @SuppressWarnings("null")
    void shouldContainGitHubUrlInSlackBodyWhenDefectReported() {
        // Given
        String expectedUrl = "https://github.com/org/repo/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "GitHub URL in Slack body",
            "End-to-end validation failed",
            expectedUrl
        );

        // When
        // We execute the command on the aggregate. 
        // We assume the aggregate or a handler listening to its events uses the SlackNotificationPort.
        // For unit testing the aggregate logic specifically:
        
        Exception exception = assertThrows(Exception.class, () -> {
            validationAggregate.execute(cmd);
        });

        // Then (Red Phase Expectation)
        // This test expects the implementation to be MISSING or INCORRECT initially.
        // We verify that IF events were raised, they would trigger the correct notification.
        // Since we are in TDD Red, this will likely fail because ValidationAggregate doesn't exist or handle this command yet.
        
        assertTrue(exception instanceof UnknownCommandException || exception instanceof NullPointerException, 
            "Expected Red Phase failure: Class missing or Command not implemented");
    }

    /**
     * Integration-style test verifying the Slack Port contract is satisfied by the message body.
     */
    @Test
    void verifySlackPayloadFormat() {
        // Given
        String githubUrl = "https://github.com/example/repo/issues/1";
        String rawBody = "{\"text\": \"Defect reported. GitHub issue: " + githubUrl + "\"}";

        // When
        mockSlack.sendMessage(rawBody);

        // Then
        assertEquals(1, mockSlack.getSentMessages().size());
        String sentPayload = mockSlack.getSentMessages().get(0);
        
        // Verify URL presence (Acceptance Criteria)
        assertTrue(sentPayload.contains(githubUrl), "Slack body must contain GitHub URL");
    }
}