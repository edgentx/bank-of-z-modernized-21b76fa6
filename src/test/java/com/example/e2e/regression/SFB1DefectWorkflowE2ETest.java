package com.example.e2e.regression;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.DefectAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regression Test for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Context: Ensures that when a defect is reported, the resulting Slack message
 * contains the direct link to the GitHub issue created.
 */
public class SFB1DefectWorkflowE2ETest {

    @Test
    void verifySlackBodyContainsGitHubUrl() {
        // Given: A mock GitHub and Slack environment
        GitHubPort mockGitHub = Mockito.mock(GitHubPort.class);
        SlackNotificationPort mockSlack = Mockito.mock(SlackNotificationPort.class);

        // And: A defect report command for "VW-454"
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454 - GitHub URL in Slack body";
        String expectedGitHubUrl = "https://github.com/mock-repo/issues/1";
        
        when(mockGitHub.createIssue(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Optional.of(expectedGitHubUrl));

        DefectAggregate aggregate = new DefectAggregate(defectId, mockGitHub, mockSlack);
        ReportDefectCommand cmd = new ReportDefectCommand(defectId, title, "E2E Validation", "LOW");

        // When: The defect is executed via the temporal-worker (simulated here by aggregate)
        aggregate.execute(cmd);

        // Then: Verify the Slack body includes "GitHub issue: <url>"
        verify(mockSlack).sendNotification(contains("GitHub issue: " + expectedGitHubUrl));
    }
}
