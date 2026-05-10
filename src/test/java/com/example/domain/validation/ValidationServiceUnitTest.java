package com.example.domain.validation;

import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure JUnit Test for ValidationService.
 * Complements the Cucumber test by verifying edge cases directly.
 */
class ValidationServiceUnitTest {

    private ValidationService validationService;
    private MockSlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
        validationService = new ValidationService(mockSlack);
    }

    @Test
    void whenReportingDefect_shouldSendSlackNotificationContainingUrl() {
        // Given
        String defectId = "VW-454";
        String url = "https://github.com/example/repo/issues/123";

        // When
        validationService.reportDefect(defectId, url);

        // Then
        assertTrue("Mock should have been triggered", mockSlack.wasCalled());
        
        String body = mockSlack.getLastMessageBody();
        assertNotNull("Body should not be null", body);
        
        // RED PHASE FAILURE: The current implementation only returns "Defect: VW-454"
        // This test expects the URL to be present. It will fail.
        assertTrue("Body should contain the GitHub URL: " + url, body.contains(url));
    }

    @Test
    void whenReportingDefect_shouldSendToCorrectChannel() {
        // When
        validationService.reportDefect("VW-100", "http://dummy.url");

        // Then
        assertEquals("#vforce360-issues", mockSlack.getLastChannel());
    }
}
