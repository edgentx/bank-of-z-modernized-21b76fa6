package com.example.domain.vforce360;

import com.example.Application;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies that triggering a defect report via Temporal results in a Slack message
 * containing the GitHub issue URL.
 *
 * Corresponds to Story S-FB-1.
 */
@SpringBootTest
@ContextConfiguration(classes = DefectReportSFB1E2ETest.TestConfig.class)
public class DefectReportSFB1E2ETest {

    @Autowired
    private MockSlackNotificationPort mockSlack;

    // NOTE: In a real Temporal test environment, we would inject a TestWorkflowEnvironment.
    // For this structural TDD phase, we simulate the workflow execution logic via
    // a direct invocation of the service/orchestrator that would handle the command.
    // This satisfies the "Red Phase" requirement by failing until implementation exists.

    @BeforeEach
    public void setUp() {
        mockSlack.reset();
    }

    @Test
    public void testReportDefect_sendsSlackMessageContainingGithubUrl() {
        // Arrange
        String defectId = "VW-454";
        String expectedChannel = "#vforce360-issues";
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        String description = "Validating VW-454 — GitHub URL in Slack body";

        // Act
        // Simulating the Temporal activity execution or service call
        // This class/interface will be created in the implementation phase
        // ReportDefectWorkflowOrchestrator.report(mockSlack, defectId, expectedUrl, description);
        
        // For now, we call a placeholder static method to simulate the trigger
        // This ensures the test compiles but fails because the method doesn't exist.
        try {
            // We assume the implementation will exist in a class named 'DefectReporter' in the main package.
            // This is a placeholder for the implementation that will make this test green.
             Class<?> clazz = Class.forName("com.example.services.DefectReporter");
             java.lang.reflect.Method method = clazz.getMethod("report", SlackNotificationPort.class, String.class, String.class, String.class);
             method.invoke(null, mockSlack, defectId, expectedUrl, description);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // Red Phase: We want this to fail explicitly if implementation is missing
            fail("Implementation 'com.example.services.DefectReporter' not found. TDD Red Phase: Implementation required.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Assert
        List<MockSlackNotificationPort.SentMessage> messages = mockSlack.getMessages();
        assertFalse(messages.isEmpty(), "Slack should have been called");
        
        MockSlackNotificationPort.SentMessage msg = messages.get(0);
        assertEquals(expectedChannel, msg.channel, "Should notify #vforce360-issues");
        
        // AC: Verify Slack body contains GitHub issue link
        assertTrue(msg.body.contains(expectedUrl), 
            "Slack body must contain the GitHub URL: " + expectedUrl + "\nActual body: " + msg.body);
        
        assertTrue(msg.body.contains(defectId), "Slack body should reference the defect ID");
    }

    @Configuration
    @Import(Application.class)
    static class TestConfig {
        @Bean
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }
    }
}
