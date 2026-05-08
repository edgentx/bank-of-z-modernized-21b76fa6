package com.example.e2e.regression;

import com.example.application.DefectReportingService;
import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.mocks.MockDefectRepositoryPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Validates that triggering a defect report results in a Slack message
 * containing the GitHub issue URL.
 *
 * Context: VForce360 PM diagnostic conversation.
 * Runner: JUnit 5 (Spring Boot standard)
 */
class VW454SlackBodyValidationTest {

    private MockSlackNotificationPort slackMock;
    private MockDefectRepositoryPort repoMock;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        slackMock = new MockSlackNotificationPort();
        repoMock = new MockDefectRepositoryPort();
        service = new DefectReportingService(repoMock, slackMock);
    }

    @Test
    void shouldContainGitHubUrlInSlackBody_WhenDefectIsReported() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Validating GitHub URL in Slack body",
            expectedGitHubUrl,
            "LOW"
        );

        // Act
        // Simulating the temporal-worker exec trigger
        service.reportDefect(cmd);

        // Assert
        assertEquals(1, slackMock.messages.size(), "Slack should have received one message");

        MockSlackNotificationPort.CapturedMessage msg = slackMock.messages.get(0);
        assertEquals("#vforce360-issues", msg.channel(), "Message should be sent to the correct channel");

        // Critical Assertion: Body must contain the URL
        assertTrue(
            msg.body().contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL. Found: " + msg.body()
        );
    }

    @Test
    void shouldContainFormattedUrlLineInSlackBody() {
        // Arrange
        String url = "https://github.com/bank-of-z/vforce360/issues/1";
        ReportDefectCmd cmd = new ReportDefectCmd("S-FB-1", "Fix defect", url, "MEDIUM");

        // Act
        service.reportDefect(cmd);

        // Assert
        MockSlackNotificationPort.CapturedMessage msg = slackMock.messages.get(0);
        // Verify the specific format expectation if necessary, e.g. "GitHub Issue: <url>"
        assertTrue(
            msg.body().contains("GitHub Issue:"),
            "Slack body should clearly identify the GitHub link."
        );
        assertTrue(
            msg.body().contains(url),
            "Slack body should append the actual URL after the label."
        );
    }

    @Test
    void shouldRejectDefectReport_WhenGitHubUrlIsMissing() {
        // Arrange
        ReportDefectCmd invalidCmd = new ReportDefectCmd(
            "VW-999",
            "Missing URL",
            "", // Empty URL
            "HIGH"
        );

        // Act & Assert
        // The aggregate should throw an exception before Slack is called
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.reportDefect(invalidCmd);
        });

        assertTrue(exception.getMessage().contains("GitHub URL is required"));
        assertEquals(0, slackMock.messages.size(), "No Slack message should be sent if validation fails");
    }
}
