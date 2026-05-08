package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.SlackNotificationPostedEvent;
import com.example.mocks.InMemoryValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Regression test for VW-454.
 * Validates that when a defect is reported, the resulting Slack event payload
 * contains the required GitHub issue URL.
 */
class VW454SlackLinkValidationTest {

    private static final String DEFECT_ID = "VW-454";
    private static final String SLACK_CHANNEL = "#vforce360-issues";
    private static final String GITHUB_URL = "https://github.com/bank-of-z/vforce/issues/454";

    private ValidationAggregate aggregate;

    @BeforeEach
    void setUp() {
        aggregate = new ValidationAggregate(DEFECT_ID);
    }

    @Test
    void shouldContainGitHubUrlInSlackBodyWhenDefectReported() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            DEFECT_ID,
            "Slack body missing GitHub link",
            "LOW",
            GITHUB_URL
        );

        // When
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size(), "One event should be produced");
        
        assertTrue(events.get(0) instanceof SlackNotificationPostedEvent, "Event should be SlackNotificationPostedEvent");
        
        SlackNotificationPostedEvent slackEvent = (SlackNotificationPostedEvent) events.get(0);
        
        assertNotNull(slackEvent.body(), "Slack body should not be null");
        
        // Critical assertion for VW-454 regression
        assertTrue(
            slackEvent.body().contains(GITHUB_URL),
            "Slack body must contain the GitHub issue URL: " + GITHUB_URL
        );
        
        assertTrue(
            slackEvent.body().contains("GitHub issue:"),
            "Slack body should explicitly label the GitHub URL"
        );
    }

    @Test
    void shouldThrowExceptionIfGitHubUrlIsMissing() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            DEFECT_ID,
            "Missing URL",
            "LOW",
            null // URL is null
        );

        // When & Then
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> aggregate.execute(cmd)
        );

        assertTrue(ex.getMessage().contains("GitHub URL"));
    }

    @Test
    void shouldThrowUnknownCommandForUnsupportedCommand() {
        // Given
        Command unknownCmd = new Command() {}; // Anonymous command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}
