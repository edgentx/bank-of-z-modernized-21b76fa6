package com.example.domain.validation;

import com.example.ports.SlackNotifier;
import com.example.ports.GitHubIssueTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * S-FB-1: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 *
 * Test Class: DefectReportValidationTest
 * Description: Verifies that when reporting a defect via the temporal-worker execution path,
 * the resulting Slack notification body contains the GitHub issue URL.
 */
public class DefectReportValidationTest {

    private MockGitHubIssueTracker mockGitHub;
    private MockSlackNotifier mockSlack;
    private DefectReportService service;

    @BeforeEach
    public void setUp() {
        mockGitHub = new MockGitHubIssueTracker();
        mockSlack = new MockSlackNotifier();
        service = new DefectReportService(mockGitHub, mockSlack);
    }

    @Test
    public void testReportDefect_ShouldContainGitHubUrlInSlackBody() {
        // Given: A defect report for VW-454
        String defectTitle = "VW-454: GitHub URL missing";
        String defectBody = "The Slack notification does not include the link.";
        String defectLabel = "bug";

        // We configure the Mock GitHub to return a predictable URL
        // Simulating the creation of https://github.com/example/repo/issues/123
        mockGitHub.setNextIssueUrl("https://github.com/example/repo/issues/123");

        // When: The defect report is executed via the temporal-worker logic
        service.reportDefect(defectTitle, defectBody, defectLabel);

        // Then: The Slack body must include the GitHub URL
        // We verify the state of the Mock to see what was actually passed
        String actualSlackBody = mockSlack.getCapturedBody();
        String expectedUrl = "https://github.com/example/repo/issues/123";

        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Expected Slack body to contain GitHub issue URL: " + expectedUrl + ", but got: " + actualSlackBody
        );
    }

    @Test
    public void testReportDefect_MissingGitHubUrl_ShouldFailAssertion() {
        // Regression test for VW-454
        // Given: GitHub service returns a URL
        mockGitHub.setNextIssueUrl("https://github.com/example/repo/issues/454");

        // When: Reporting defect
        service.reportDefect("Regression", "Body", "bug");

        // Then: Verify the URL is present. If the implementation is broken (red phase), this fails.
        String body = mockSlack.getCapturedBody();
        assertTrue(body.contains("https://github.com/example/repo/issues/454"), "Regression check: URL missing in Slack body");
    }
}
