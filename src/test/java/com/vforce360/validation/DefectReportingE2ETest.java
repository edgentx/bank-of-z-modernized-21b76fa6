package com.vforce360.validation;

import com.vforce360.validation.ports.*;
import com.vforce360.validation.mocks.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * End-to-End Regression Test for Story VW-454.
 * 
 * Validates that when _report_defect is triggered via temporal-worker exec,
 * the resulting Slack body contains the correctly formatted GitHub issue link.
 * 
 * RED PHASE: Implementation is assumed missing/broken until proven passing.
 */
@SpringBootTest
class DefectReportingE2ETest {

    @Autowired
    private DefectReportOrchestrator orchestrator;

    // We use Mock Beans for external dependencies to ensure isolated behavior
    @MockBean
    private TemporalWorkflowPort temporalPort;

    @MockBean
    private GitHubRestClientPort githubPort;

    @MockBean
    private SlackNotificationPort slackPort;

    @MockBean
    private ValidationRepositoryPort repositoryPort;

    private final String TEST_DEFECT_TITLE = "VW-454: GitHub URL missing";
    private final String GITHUB_ISSUE_URL = "https://github.com/21b76fa6-afb6-4593-9e1b-b5d7548ac4d1/issues/454";

    @BeforeEach
    void setUp() {
        // Reset mocks before each test to ensure isolation
        reset(temporalPort, githubPort, slackPort, repositoryPort);
    }

    @Test
    void whenReportDefectTriggered_thenSlackBodyContainsGitHubUrl() {
        // 1. Setup Temporal Workflow Trigger Simulation
        DefectReportCommand command = new DefectReportCommand(TEST_DEFECT_TITLE, "Validation failed", Severity.LOW);
        
        // Mock GitHub response: The API returns the issue URL
        when(githubPort.createIssue(any())).thenReturn(GITHUB_ISSUE_URL);

        // 2. Execute the workflow trigger
        // In the actual system, this is an async signal, but for testing we simulate the execution path
        orchestrator.executeReportDefect(command);

        // 3. Verify the Interaction
        // We verify that the Slack Port was called with a payload containing the specific URL
        verify(slackPort).sendMessage(argThat(payload -> 
            payload.getBody() != null 
            && payload.getBody().contains(GITHUB_ISSUE_URL)
        ));
    }

    @Test
    void whenGitHubUrlReturned_itIsProperlyFormatted() {
        // Edge case: Ensure URL is not just a substring but the actual link line format
        DefectReportCommand command = new DefectReportCommand(TEST_DEFECT_TITLE, "Check URL format", Severity.LOW);
        
        when(githubPort.createIssue(any())).thenReturn(GITHUB_ISSUE_URL);

        orchestrator.executeReportDefect(command);

        // Verify the body includes the expected label, not just the raw URL string in a mess
        verify(slackPort).sendMessage(argThat(payload -> 
            payload.getBody().contains("GitHub issue: " + GITHUB_ISSUE_URL) ||
            payload.getBody().contains("<" + GITHUB_ISSUE_URL + ">") // Slack link format
        ));
    }
}
