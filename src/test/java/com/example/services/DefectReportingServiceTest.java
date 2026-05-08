package com.example.services;

import com.example.adapters.GitHubPortImpl; // Dummy class to verify Port type usage
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for DefectReportingService verifying the URL propagation.
 * Story: S-FB-1
 */
class DefectReportingServiceTest {

    @Test
    void testReportDefect_shouldPostToSlackWithGitHubUrl() {
        // 1. Setup Mocks
        GitHubPort mockGitHubPort = mock(GitHubPort.class);
        SlackNotificationPort mockSlackPort = mock(SlackNotificationPort.class);

        // 2. Define Mock Behavior
        String expectedGitHubUrl = "https://github.com/mock-org/repo/issues/454";
        when(mockGitHubPort.createIssue(anyString(), anyString())).thenReturn(expectedGitHubUrl);

        // 3. Inject into Service
        // Note: In real Spring this would be @Autowired. Here we simulate.
        // Using a lambda or wrapper to simulate the service logic since we are in TDD Red.
        DefectReportingService service = new DefectReportingService(mockGitHubPort, mockSlackPort);

        // 4. Execute
        String defectTitle = "VW-454 — GitHub URL in Slack body";
        String defectBody = "Defect details...";
        service.reportDefect(defectTitle, defectBody);

        // 5. Verify Slack Call (The Critical Validation)
        // We assert that sendMessage was called with a specific channel AND
        // that the message 'contains' the GitHub URL returned by the first step.
        verify(mockSlackPort).sendMessage(
            eq("#vforce360-issues"),
            contains(expectedGitHubUrl), // This checks the Actual Behavior vs Expected
            eq(Set.of())
        );
    }

    @Test
    void testReportDefect_shouldCreateGitHubIssueFirst() {
        // Validates the Temporal workflow step 1
        GitHubPort mockGitHubPort = mock(GitHubPort.class);
        SlackNotificationPort mockSlackPort = mock(SlackNotificationPort.class);

        when(mockGitHubPort.createIssue(anyString(), anyString())).thenReturn("http://dummy.url");

        DefectReportingService service = new DefectReportingService(mockGitHubPort, mockSlackPort);

        service.reportDefect("Title", "Body");

        verify(mockGitHubPort).createIssue(eq("Title"), eq("Body"));
    }
}