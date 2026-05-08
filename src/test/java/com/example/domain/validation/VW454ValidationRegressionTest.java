package com.example.domain.validation;

import com.example.Application;
import com.example.mocks.InMemoryVForce360NotificationPort;
import com.example.ports.VForce360NotificationPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test.
 * Story: S-FB-1
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end).
 * 
 * This test verifies that when a defect is reported, the resulting Slack body
 * contains the expected GitHub issue URL.
 * 
 * Currently expected to FAIL (Red) because the implementation is missing or incorrect.
 */
@SpringBootTest
@ContextConfiguration(classes = VW454ValidationRegressionTest.TestConfig.class)
class VW454ValidationRegressionTest {

    @Autowired
    private InMemoryVForce360NotificationPort mockNotificationPort;

    @Autowired
    private ReportDefectWorkflowOrchestrator orchestrator;

    @Configuration
    @Import(Application.class)
    static class TestConfig {
        @Bean
        public VForce360NotificationPort vForce360NotificationPort() {
            return new InMemoryVForce360NotificationPort();
        }
        
        @Bean
        public ReportDefectWorkflowOrchestrator reportDefectWorkflowOrchestrator(VForce360NotificationPort port) {
            return new ReportDefectWorkflowOrchestrator(port);
        }
    }

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";

        // Act
        // Trigger the report_defect logic via the orchestrator
        // which simulates the temporal-worker exec
        orchestrator.reportDefect(defectId);

        // Assert
        // Verify the notification port received the call
        assertEquals(1, mockNotificationPort.getMessages().size(), "Notification should be sent once");

        InMemoryVForce360NotificationPort.SentMessage msg = mockNotificationPort.getMessages().get(0);
        assertEquals(defectId, msg.defectId, "Defect ID should match");

        // The critical validation: The body must contain the GitHub URL.
        // This will fail (RED) until the workflow logic is fixed to include the URL.
        assertTrue(
            msg.body.contains(expectedUrl), 
            "Slack body must contain GitHub issue URL: " + expectedUrl + "\nActual body: " + msg.body
        );
    }
}
