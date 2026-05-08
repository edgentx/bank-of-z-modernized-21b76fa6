package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.port.SlackNotificationPort;
import com.example.domain.validation.port.GitHubIssuePort;
import com.example.domain.validation.port.SlackMessage;
import com.example.mocks.InMemoryValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefectReportVW454Test {

    @Mock
    private GitHubIssuePort gitHubPort;

    @Mock
    private SlackNotificationPort slackPort;

    private InMemoryValidationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryValidationRepository();
    }

    /**
     * AC: Regression test added to e2e/regression/ covering this scenario
     * Story: Defect Validating VW-454 — GitHub URL in Slack body (end-to-end)
     * Expected: Slack body includes GitHub issue: <url>
     */
    @Test
    void shouldIncludeGitHubIssueUrlInSlackBodyWhenReportingDefect() {
        // Arrange
        String defectId = "VW-454";
        String expectedGitHubUrl = "https://github.com/bank-of-z/issues/454";
        
        // We mock the external dependencies (GitHub and Slack) as required
        when(gitHubPort.createIssue(any(), any(), any())).thenReturn(expectedGitHubUrl);
        when(slackPort.sendMessage(any())).thenReturn("OK");

        // Act
        DefectReportAggregate aggregate = new DefectReportAggregate(defectId, gitHubPort, slackPort);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "Validation error in Slack body", 
            "LOW", 
            "validation"
        );
        
        List events = aggregate.execute(cmd);

        // Assert
        // 1. Verify processing occurred
        assertFalse(events.isEmpty(), "Processing should produce domain events");

        // 2. Verify GitHub was called (part of the logic flow)
        verify(gitHubPort).createIssue(
            eq("[VW-454] Validation error in Slack body"),
            contains("Severity: LOW"),
            eq("bug")
        );

        // 3. CRITICAL ASSERTION for VW-454: Verify Slack body contains the GitHub URL
        verify(slackPort).sendMessage(argThat(message -> {
            String body = message.body();
            return body != null 
                && body.contains("GitHub issue")
                && body.contains(expectedGitHubUrl);
        }));
    }
}
