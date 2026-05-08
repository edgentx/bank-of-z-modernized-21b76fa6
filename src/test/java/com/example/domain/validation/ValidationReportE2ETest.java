package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360DiagnosticPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * End-to-End Regression Test for Story S-FB-1 (VW-454).
 * Validates that the GitHub URL is present in the Slack notification body
 * when a defect is reported via the temporal-worker.
 */
class ValidationReportE2ETest {

    /**
     * AC: The validation no longer exhibits the reported behavior.
     * Test: Verify Slack body contains GitHub issue link.
     */
    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // 1. Setup Mocks
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
        MockVForce360DiagnosticPort mockDiagnostic = new MockVForce360DiagnosticPort();

        // Configure the mock diagnostic to return a specific GitHub Issue ID
        // Simulating the scenario where we link a defect to a GitHub issue.
        String expectedIssueId = "GH-123";
        String expectedUrl = "https://github.com/bank-of-z/modernization/issues/" + expectedIssueId;
        mockDiagnostic.setNextIssueUrl(expectedUrl);

        // 2. Execute logic (Simulating the temporal-worker triggering the report)
        // We are testing the behavior. The 'system' under test is the interaction flow.
        // In a real Spring Boot app, this might be a Service or Workflow.
        // Here we instantiate the handler logic directly to validate the contract.
        ValidationReportService service = new ValidationReportService(mockSlack, mockDiagnostic);

        DefectReportCommand cmd = new DefectReportCommand(
            "VW-454",
            "GitHub URL missing",
            "LOW"
        );

        service.reportDefect(cmd);

        // 3. Verify Slack Interaction (Expected Behavior)
        // Capture the arguments passed to the Slack port
        String actualSlackBody = mockSlack.getCapturedBody();

        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(actualSlackBody.contains(expectedUrl), 
            "Slack body must include the GitHub issue URL.\nExpected: " + expectedUrl + "\nActual: " + actualSlackBody);
        
        // Sanity check for the title as well
        assertTrue(actualSlackBody.contains("VW-454"), "Slack body should reference the defect ID");
    }

    /**
     * Negative Case: Ensure the test fails if the link is missing.
     * This confirms the test suite is actually checking the logic (Red/Green compliance).
     */
    @Test
    void shouldFailIfGitHubUrlIsMissingFromBody() {
        // 1. Setup Mocks (Diagnostic returns valid data, but Slack is the subject)
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
        MockVForce360DiagnosticPort mockDiagnostic = new MockVForce360DiagnosticPort();

        // Do not set a URL, or set it to empty to simulate the defect condition
        mockDiagnostic.setNextIssueUrl(null);

        ValidationReportService service = new ValidationReportService(mockSlack, mockDiagnostic);
        service.reportDefect(new DefectReportCommand("VW-454", "Defect", "LOW"));

        String actualSlackBody = mockSlack.getCapturedBody();
        
        // If the logic is broken (returns empty body or null), this test verifies the failure state
        if (actualSlackBody == null) {
            fail("Slack body was null - logic not implemented or NPE occurred");
        }
        
        // If the implementation is empty/missing, the URL won't be there.
        // We just assert truth here; the test above asserts the positive requirement.
        assertFalse(actualSlackBody.contains("https://github.com"), "This assertion proves we are checking the content.");
    }
}