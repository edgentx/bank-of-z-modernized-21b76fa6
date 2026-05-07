package com.example.domain.validation;

import com.example.domain.validation.ports.DefectReporter;
import com.example.mocks.MockSlackPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1.
 * Verifies that when a defect is reported via Temporal/Worker,
 * the resulting Slack notification body contains the GitHub issue URL.
 */
@SpringBootTest(classes = ValidationTestConfig.class)
public class DefectReportTest {

    @Autowired
    private DefectReporter defectReporter;

    @Autowired
    private MockSlackPublisher mockSlackPublisher;

    @BeforeEach
    void setup() {
        mockSlackPublisher.reset();
    }

    @Test
    void testDefectReportIncludesGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL missing in Slack body";
        String defectDescription = "Reproduction steps...";
        String expectedGitHubUrl = "https://github.com/egdcrypto/bank-of-z-modernized/issues/454";

        // Act (Triggering _report_defect via temporal-worker exec)
        // This call internally creates the GitHub issue and then publishes to Slack.
        defectReporter.reportDefect(defectTitle, defectDescription);

        // Assert (Verify Slack body contains GitHub issue link)
        assertNotNull(mockSlackPublisher.getLastMessageBody(), "Slack body should not be null");
        assertTrue(
            mockSlackPublisher.lastMessageContains(expectedGitHubUrl),
            "Slack body should include the GitHub issue URL: " + expectedGitHubUrl + "\nActual: " + mockSlackPublisher.getLastMessageBody()
        );
    }
}