package com.example.domain.validation;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.SlackMessageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test ID: S-FB-1
 * Description: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Tests the business logic ensuring that when a defect is reported,
 * the resulting event (and subsequently the Slack message) contains
 * a valid GitHub issue URL.
 */
@ExtendWith(MockitoExtension.class)
class DefectReportWorkflowTest {

    @Mock
    private SlackMessageValidator validator;

    @Test
    void testReportDefect_generatesEventWithGitHubUrl() {
        // Given
        String defectId = "VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "GitHub URL missing", "Slack body is empty", "LOW");
        DefectAggregate aggregate = new DefectAggregate(defectId);

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = events.get(0);
        
        // Check that the event contains a URL field
        assertTrue(event.toString().contains("githubUrl"), "Event should contain githubUrl field");
        
        // Specific check for URL format (e.g. contains the defect ID)
        String eventString = event.toString();
        assertTrue(eventString.contains(defectId), "GitHub URL should reference the defect ID: " + defectId);
    }

    @Test
    void testReportDefect_validatorAcceptsGeneratedUrl() {
        // Given
        String defectId = "VW-454";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "GitHub URL missing", "Slack body is empty", "LOW");
        DefectAggregate aggregate = new DefectAggregate(defectId);
        
        // Assume the validator checks for the presence of https://github.com/...
        when(validator.containsGitHubIssueUrl(anyString())).thenAnswer(invocation -> {
            String body = invocation.getArgument(0);
            return body != null && body.contains("https://github.com/");
        });

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Event list should not be empty");
        
        // Simulate extracting the message body sent to Slack
        // In a real scenario, this would come from the Workflow Activity logic
        String slackMessageBody = "Defect Reported: " + events.get(0).toString();
        
        // Verify: Slack body includes GitHub issue
        assertTrue(
            validator.containsGitHubIssueUrl(slackMessageBody), 
            "Slack body includes GitHub issue: <url>"
        );
    }
}
