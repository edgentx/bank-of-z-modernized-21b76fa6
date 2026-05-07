package com.example.e2e.regression;

import com.example.Application;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import com.example.domain.shared.Command;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * E2E Regression Test for Story S-FB-1 (Defect VW-454).
 * 
 * Validates that when a defect is reported:
 * 1. A GitHub issue is created.
 * 2. The resulting URL is included in the Slack notification body.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class VW454_SlackUrlValidationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockGitHubIssuePort mockGitHubIssuePort;

    // We spy on the mock to capture arguments
    @Autowired
    private MockSlackNotificationPort mockSlackNotificationPort;

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // --- ARRANGE ---
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        mockGitHubIssuePort.setResponseUrl(expectedUrl);

        // This is the hypothetical workflow/activity that implements the logic
        // We are testing the integration. If the service doesn't exist yet,
        // this test fails compilation (Red Phase).
        DefectReportingOrchestrator orchestrator = applicationContext.getBean(DefectReportingOrchestrator.class);

        // --- ACT ---
        // Trigger the report_defect flow
        // We need a Command. Assuming ReportDefectCmd exists or is a generic wrapper.
        // For the sake of the TDD red phase, we assume this class needs to be created.
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Validation failed", "LOW");
        
        orchestrator.execute(cmd);

        // --- ASSERT ---
        // 1. Verify GitHub was called
        // (Implicitly checked by the fact that Slack has the URL, but explicit is good too)
        
        // 2. Verify Slack was called and the URL is in the body
        // Note: Since we are mocking the ports, we need to verify the interaction.
        // The 'MockSlackNotificationPort' needs to expose what it received.
        
        // The actual assertion logic depends on how the mock captures data.
        // Assuming we can retrieve the last payload sent to Slack:
        
        String slackBody = mockSlackNotificationPort.getLastReceivedBody();
        
        assertNotNull(slackBody, "Slack payload should not be null");
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL: " + expectedUrl + "\nActual Body: " + slackBody
        );
    }

    @Test
    void testSlackBodyHandlesGitHubFailure() {
        // --- ARRANGE ---
        mockGitHubIssuePort.setSimulatedFailure(true);
        
        DefectReportingOrchestrator orchestrator = applicationContext.getBean(DefectReportingOrchestrator.class);
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "Validation failed", "LOW");

        // --- ACT & ASSERT ---
        assertThrows(Exception.class, () -> orchestrator.execute(cmd));
    }
}
