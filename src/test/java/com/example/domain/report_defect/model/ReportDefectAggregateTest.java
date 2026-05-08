package com.example.domain.report_defect.model;

import com.example.domain.report_defect.model.ReportDefectAggregate;
import com.example.domain.report_defect.model.ReportDefectPostedEvent;
import com.example.domain.report_defect.port.SlackNotificationPort;
import com.example.domain.report_defect.port.GithubIssuePort;
import com.example.domain.report_defect.port.GithubIssueResponse;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Aggregate logic tests for S-FB-1.
 * Verifying that the ReportDefect command generates a valid Slack payload
 * that includes the GitHub URL.
 */
class ReportDefectAggregateTest {

    private MockGithubPort githubPort;
    private MockSlackPort slackPort;

    @BeforeEach
    void setUp() {
        githubPort = new MockGithubPort();
        slackPort = new MockSlackPort();
    }

    @Test
    void shouldExecuteReportDefectCommandAndGenerateEventWithGithubUrl() {
        // Arrange
        String defectId = "VW-454";
        String description = "Slack body missing GitHub link";
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        
        // Configure Mock behavior: The GitHub adapter will return this specific URL
        githubPort.setMockUrl(expectedUrl);
        
        var aggregate = new ReportDefectAggregate(defectId, githubPort, slackPort);
        var cmd = new ReportDefectCommand(defectId, description, Severity.LOW, "validation");

        // Act
        // In a real reactive/domain model, this returns events. 
        // The actual 'side effect' of calling Slack happens in the handler listening to the event, 
        // or synchronously within the aggregate if we treat it as a use-case coordinator.
        // For S-FB-1 validation, we check the aggregate produces the correct state/event data.
        List events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should generate a defect posted event");
        assertTrue(events.get(0) instanceof ReportDefectPostedEvent, "Event type mismatch");
        
        var event = (ReportDefectPostedEvent) events.get(0);
        assertEquals(defectId, event.aggregateId());
        assertEquals(expectedUrl, event.githubIssueUrl(), "Event must contain the GitHub URL");
    }

    @Test
    void shouldFailWhenReportingDefectWithoutId() {
        var aggregate = new ReportDefectAggregate("ID", githubPort, slackPort);
        var cmd = new ReportDefectCommand(null, "Desc", Severity.LOW, "comp");

        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void shouldFailWhenReportingDefectWithBlankDescription() {
        var aggregate = new ReportDefectAggregate("ID", githubPort, slackPort);
        var cmd = new ReportDefectCommand("ID", "   ", Severity.LOW, "comp");

        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
