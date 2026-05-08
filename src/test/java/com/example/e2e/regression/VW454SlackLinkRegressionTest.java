package com.example.e2e.regression;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.service.DefectReportingService; // Assumes existence of the class we are testing
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454: GitHub URL in Slack body.
 * ID: S-FB-1
 *
 * Context: Ensures that when _report_defect is triggered,
 * the resulting Slack body contains the GitHub issue URL.
 *
 * Phase: RED (Tests written before implementation logic is verified)
 */
@SpringBootTest
class VW454SlackLinkRegressionTest {

    // We inject the mock via Spring configuration or manual setup for the test context.
    // For the purpose of this TDD exercise, we assume manual wiring or a specific TestConfig.
    private final SlackNotificationPort slackPort = new MockSlackNotificationPort();
    private DefectReportingService service;

    private static final String EXPECTED_GITHUB_URL_PREFIX = "https://github.com";
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    @BeforeEach
    void setUp() {
        // Manual DI for the test. In a real Spring Boot test, we might use @MockBean.
        // Here we instantiate the System Under Test (SUT) with its dependencies.
        // Note: Since DefectReportingService implementation doesn't exist yet,
        // this compilation failure is expected in TDD. We proceed by defining the shape.
        service = new DefectReportingService(slackPort);
    }

    @Test
    void testReportDefect_generatesSlackMessageContainingGitHubLink() {
        // Arrange
        String defectId = "VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd(
                defectId,
                "Defect: Validating VW-454 — GitHub URL in Slack body",
                "Checking #vforce360-issues for the link line",
                Map.of("source", "VForce360 PM diagnostic")
        );

        // Act
        // Trigger the report_defect flow via the service
        service.handle(cmd);

        // Assert
        // Verify that the Slack port was called
        MockSlackNotificationPort mockPort = (MockSlackNotificationPort) slackPort;
        assertEquals(1, mockPort.getMessages().size(), "Slack should have been called exactly once");

        MockSlackNotificationPort.SentMessage sent = mockPort.getLastMessage();
        assertEquals(SLACK_CHANNEL, sent.channel, "Message should be sent to the specific channel");

        // The core assertion for VW-454: The body MUST contain the URL
        // We look for the presence of a valid GitHub URL format
        assertTrue(
            sent.body.contains(EXPECTED_GITHUB_URL_PREFIX),
            "Slack body must include the GitHub issue URL (starting with " + EXPECTED_GITHUB_URL_PREFIX + "). " +
            "Actual body: " + sent.body
        );
    }

    @Test
    void testReportDefect_InvalidCommand_ThrowsException() {
        // Arrange
        ReportDefectCmd invalidCmd = new ReportDefectCmd(null, null, null, null);

        // Act & Assert
        // Assuming the service validates the command
        assertThrows(IllegalArgumentException.class, () -> {
            service.handle(invalidCmd);
        });
    }
}
