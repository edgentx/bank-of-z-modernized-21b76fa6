package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.ports.SlackNotifier;
import com.example.ports.TicketingSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlackNotificationValidationTest {

    @Mock
    private SlackNotifier slackNotifier;

    @Mock
    private TicketingSystem ticketingSystem;

    /*
     * Test Case: Validate Slack body contains GitHub URL after defect reporting.
     * Corresponds to: Defect VW-454
     * Scenario:
     *   1. Trigger _report_defect via temporal-worker exec
     *   2. Verify Slack body contains GitHub issue link
     *
     * RED PHASE: This test will fail because ReportDefectHandler is not yet implemented
     * or does not correctly format the Slack message with the ticket URL.
     */
    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL in Slack body";
        String defectDescription = "User reported that the Slack notification does not contain the link.";
        String expectedTicketUrl = "https://github.com/bank-of-z/issues/454";
        String expectedChannel = "#vforce360-issues";

        // We mock the ticketing system to return a specific URL
        when(ticketingSystem.createIssue(eq(defectTitle), eq(defectDescription)))
            .thenReturn(expectedTicketUrl);

        // We use the real handler (which needs to be created/fixed) wired with mocks
        ReportDefectHandler handler = new ReportDefectHandler(slackNotifier, ticketingSystem);

        // Act
        handler.report(defectTitle, defectDescription);

        // Assert
        // The critical assertion: verify the Slack payload contains the URL returned by the ticketing system
        verify(slackNotifier).send(
            eq(expectedChannel),
            contains(expectedTicketUrl) // This is the failing check for VW-454
        );
    }

    /*
     * Test Case: Verify null ticket URL is handled or Slack payload is correctly formatted.
     * Ensures the system doesn't break if the ticketing system returns null or unexpected data,
     * or strictly enforces that a link must be present.
     */
    @Test
    void testReportDefect_ShouldThrowExceptionIfTicketUrlNotGenerated() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL in Slack body";
        String defectDescription = "User reported that the Slack notification does not contain the link.";

        // Mock the ticketing system to return null (simulating a failure or bad state)
        when(ticketingSystem.createIssue(eq(defectTitle), eq(defectDescription)))
            .thenReturn(null);

        ReportDefectHandler handler = new ReportDefectHandler(slackNotifier, ticketingSystem);

        // Act & Assert
        // We expect a runtime exception because we can't fulfill the requirement of posting a link
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            handler.report(defectTitle, defectDescription);
        });

        assertTrue(ex.getMessage().contains("Ticket URL not generated"));
    }
}
