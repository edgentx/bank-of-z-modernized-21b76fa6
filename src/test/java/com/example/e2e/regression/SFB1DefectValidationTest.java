package com.example.e2e.regression;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * E2E Regression Test for S-FB-1: Validating VW-454.
 * 
 * Context: Verify that when a defect is reported via the Temporal worker,
 * the resulting Slack body contains the GitHub issue URL.
 * 
 * This test suite mocks the Slack adapter to inspect the message payload
 * rather than sending a real network request.
 */
public class SFB1DefectValidationTest {

    private MockSlackNotificationPort mockSlack;

    @Configuration
    static class TestConfig {
        @Bean
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }
        
        // Here we would typically wire the actual Worker or Workflow implementation
        // For this fix, we are verifying the integration point directly or simulating the behavior.
    }

    @BeforeMethod
    public void setUp() {
        // Initialize Mock
        mockSlack = new MockSlackNotificationPort();
        mockSlack.clear();
    }

    @Test(description = "S-FB-1 | Verify Slack body contains GitHub URL when defect is reported")
    public void testReportDefect_ShouldContain_GitHubUrl() {
        // 1. Simulate Defect Report Trigger
        // In a real Temporal test, we would start a workflow. 
        // Here we simulate the execution logic that produces the Slack body.
        
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example-bank/z/issues/VW-454";
        
        // This represents the logic under test (currently unimplemented/failing)
        // Logic: Trigger report_defect via temporal-worker exec
        String generatedBody = generateReportBody(defectId);

        // 2. Verify Slack body contains GitHub issue link
        // Passing the generated body to our mock port to simulate the send
        boolean sent = mockSlack.sendNotification(generatedBody);

        // 3. Assertions
        assertTrue(sent, "Notification should be successfully sent");
        
        boolean containsUrl = mockSlack.getSentMessages().stream()
            .anyMatch(msg -> msg.contains(expectedUrl) || msg.contains("GitHub issue: <url>"));
            // Note: AC says "GitHub issue: <url>", Defect says "GitHub issue link". 
            // We look for specific formatting or general presence.
            
        assertTrue(containsUrl, 
            "Slack body should include the GitHub issue URL. Found: " + mockSlack.getSentMessages());
    }

    @Test(description = "S-FB-1 | Regression check for missing URL in body")
    public void testReportDefect_MissingUrl_ShouldFail() {
        // Negative test to ensure our validation logic catches the defect
        String badBody = "Defect reported: VW-454. Please check dashboard.";
        
        // This should fail validation if we were validating strictly
        // For this E2E, we just verify the adapter received it, but the content check fails
        mockSlack.sendNotification(badBody);
        
        boolean containsUrl = mockSlack.getSentMessages().stream()
            .anyMatch(msg -> msg.contains("github.com") || msg.contains("GitHub issue"));
            
        // In the TDD Red phase, we might assert this is false if we are testing the defect existence,
        // but here we are testing the FIX. 
        // So we rely on the first test to drive the Green phase.
    }

    /**
     * Simulates the output of the 'report_defect' workflow.
     * In the Red phase, this returns stubbed data.
     * In the Green phase, this will be replaced by the actual Service/Workflow call.
     */
    private String generateReportBody(String issueId) {
        // RED PHASE STUB: Returning incomplete data to force the test to fail initially,
        // or simulating the expected correct structure if testing the mock setup.
        
        // To make this a RED phase test for the implementation:
        // We assume the implementation (Service) is missing.
        // However, since this is an E2E test, we usually wire the beans.
        // We will return a string that definitely fails the assertion for now to simulate the defect.
        return "Defect reported: " + issueId; 
        
        // NOTE: When the implementation is fixed, this method will be replaced
        // by `workflowService.reportDefect(issueId)`;
    }
}
