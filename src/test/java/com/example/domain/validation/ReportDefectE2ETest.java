package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.port.SlackNotificationPort;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.mocks.MockSlackNotificationPort;
import com.example.workflows.ReportDefectActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for Story S-FB-1.
 * Verifies that the Slack body contains the GitHub issue URL when a defect is reported.
 */
@SpringBootTest
public class ReportDefectE2ETest {

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private MockSlackNotificationPort mockSlackPort;

    @Autowired
    private ReportDefectActivity reportDefectActivity; // Temporal Activity wrapper

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        mockSlackPort.reset();
    }

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String description = "GitHub URL missing from Slack body";
        String expectedGitHubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";

        // Act
        // Simulating the Temporal Activity execution which triggers the domain logic
        reportDefectActivity.report(defectId, description);

        // Assert
        // Verify that the notification was sent
        assertTrue(mockSlackPort.wasInvoked(), "Slack notification should have been triggered");

        // Verify the body contains the specific GitHub URL for the defect
        String actualBody = mockSlackPort.getCapturedBody();
        assertNotNull(actualBody, "Slack body should not be null");
        
        // This assertion ensures the Expected Behavior is met:
        // "Slack body includes GitHub issue: <url>"
        assertTrue(
            actualBody.contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL. Expected to find: " + expectedGitHubUrl + " within: " + actualBody
        );
        
        // Additionally verify the defect ID is present for context
        assertTrue(
            actualBody.contains(defectId),
            "Slack body should reference the Defect ID"
        );
    }
}
