package com.example.domain.defect;

import com.example.domain.shared.UnknownCommandException;
import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

/**
 * TDD Red Phase: Test for VW-454.
 * Verifies that the Slack notification body for a reported defect
 * includes the URL to the created GitHub issue.
 */
public class VW454DefectReportingTest {

    private MockGitHubIssuePort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private DefectAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubIssuePort();
        mockSlack = new MockSlackNotificationPort();
        aggregate = new DefectAggregate("vw-454", mockGitHub, mockSlack);
    }

    @Test
    void whenReportingDefect_slackBodyContainsGitHubUrl() {
        // Arrange
        // The Mock GitHub is configured to return this specific URL
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        mockGitHub.setMockUrl(expectedUrl);

        ReportDefectCmd cmd = new ReportDefectCmd(
            "vw-454",
            "Validating VW-454",
            "LOW",
            "Slack body missing GitHub link"
        );

        // Act
        aggregate.execute(cmd);

        // Assert
        // The defect report process triggers the Slack notification.
        // We verify the payload received by the Slack mock contains the GitHub URL.
        String slackBody = mockSlack.getCapturedBody();

        assertNotNull(slackBody, "Slack body should not be null");
        assertTrue(
            slackBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL. Expected: " + expectedUrl + ", Got: " + slackBody
        );
    }

    @Test
    void whenReportingDefect_gitHubUrlIsNotEmpty() {
        // Arrange
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        mockGitHub.setMockUrl(expectedUrl);

        ReportDefectCmd cmd = new ReportDefectCmd("vw-454", "Title", "LOW", "Desc");

        // Act
        aggregate.execute(cmd);

        // Assert
        String slackBody = mockSlack.getCapturedBody();
        assertFalse(slackBody.isEmpty(), "Slack body should not be empty");
        assertTrue(slackBody.startsWith("http"), "URL in Slack body should look like a valid link");
    }
}
