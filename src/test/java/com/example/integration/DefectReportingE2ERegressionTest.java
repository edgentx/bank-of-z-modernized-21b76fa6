package com.example.integration;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.DefectReporterAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for VW-454.
 * Scenario: Verify that when _report_defect is triggered via temporal-worker exec,
 * the resulting Slack body contains the GitHub issue link.
 * 
 * Location: e2e/regression/
 */
class DefectReportingE2ERegressionTest {

    private MockSlackNotificationPort mockSlackPort;

    @BeforeEach
    void setUp() {
        mockSlackPort = new MockSlackNotificationPort();
    }

    @Test
    void verifyVW454_GitHubUrlInSlackBody() {
        // 1. Trigger _report_defect via temporal-worker exec (Simulated)
        String defectId = "VW-454";
        String severity = "LOW";
        String component = "validation";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedUrl = "https://github.com/bank-of-z/issues/" + defectId;

        // Assume the temporal worker executes the following logic:
        DefectReporterAggregate aggregate = new DefectReporterAggregate(defectId);
        
        // Inject Mock Port (Assuming Setter or Constructor injection for this test)
        // In a real Spring Boot test, we might use @MockBean, but strict TDD phase uses manual injection.
        injectPort(aggregate, mockSlackPort);

        ReportDefectCmd cmd = new ReportDefectCmd(defectId, severity, component, projectId, "Defect description");

        // 2. Verify Slack body contains GitHub issue link
        
        // Execute the command
        List<com.example.domain.validation.model.DomainEvent> events = aggregate.execute(cmd);

        // Simulate the side-effect (sending to Slack) which would normally happen in an ApplicationService
        assertFalse(events.isEmpty(), "Should generate an event");
        
        // Assuming the event carries the payload
        com.example.domain.validation.model.DomainEvent event = events.get(0);
        String slackPayload = event.body();

        // Validate Expected Behavior
        assertNotNull(slackPayload, "Slack payload should not be null");
        
        // Send via mock port to verify end-to-end flow
        mockSlackPort.sendNotification("#vforce360-issues", slackPayload);

        // Verification
        List<MockSlackNotificationPort.SentMessage> messages = mockSlackPort.getSentMessages();
        assertEquals(1, messages.size(), "Should send one message to Slack");
        
        String sentBody = messages.get(0).body;
        assertTrue(
            sentBody.contains(expectedUrl), 
            "Regression check for VW-454: Body must contain GitHub URL [" + expectedUrl + "]. Actual: " + sentBody
        );
    }

    // Helper to inject dependency into aggregate without modifying the Domain Class interface drastically for this test
    private void injectPort(DefectReporterAggregate aggregate, SlackNotificationPort port) {
        try {
            // In a real scenario, this would be handled by the Application Service configuration.
            // For this specific unit test, we might reflect or assume a setter if we were writing the impl.
            // However, since we are testing the OUTPUT of the aggregate (the Event), we might not need the port here.
            // But the prompt asks for E2E context covering the scenario.
            
            // If the aggregate is responsible for the side effect (not ideal, but possible in simple models):
            Field f = aggregate.getClass().getDeclaredField("slackNotificationPort");
            f.setAccessible(true);
            f.set(aggregate, port);
        } catch (Exception e) {
            // Ignore if field doesn't exist yet, as we are in Red Phase.
            // The test will fail on the assertion of the body content if the impl is empty.
        }
    }
}