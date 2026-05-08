package com.example.application;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.*;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.domain.slack.SlackMessage;
import com.example.mocks.MockSlackNotifier;
import com.example.mocks.MockValidationRepository;
import com.example.ports.SlackNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for Story S-FB-1.
 * Verifies that the ValidationWorkflowOrchestrator correctly links GitHub URLs
 * in Slack notifications when a defect is reported.
 */
class ValidationWorkflowOrchestratorTest {

    private ValidationRepository validationRepo;
    private MockSlackNotifier slackNotifier;
    private ValidationWorkflowOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        validationRepo = new MockValidationRepository();
        slackNotifier = new MockSlackNotifier();
        orchestrator = new ValidationWorkflowOrchestrator(validationRepo, slackNotifier);
    }

    @Test
    void testReportDefect_IncludesGitHubUrlInSlackBody() {
        // Arrange
        String validationId = "VW-454";
        String defectId = "DEF-102";
        String expectedUrl = "https://github.com/bank-of-z/issues/454";

        // Act
        orchestrator.reportDefect(validationId, defectId, expectedUrl);

        // Assert
        SlackMessage message = slackNotifier.getFirst();
        assertNotNull(message, "Slack message should have been sent");
        
        // AC: Slack body includes GitHub issue: <url>
        assertTrue(
            message.body().contains(expectedUrl),
            "Slack body should contain the GitHub issue URL: " + expectedUrl + ", but was: " + message.body()
        );
    }

    @Test
    void testReportDefect_PersistsValidationAggregateWithUrl() {
        // Arrange
        String validationId = "VW-455";
        String defectId = "DEF-103";
        String expectedUrl = "https://github.com/bank-of-z/issues/455";

        // Act
        orchestrator.reportDefect(validationId, defectId, expectedUrl);

        // Assert
        ValidationAggregate aggregate = validationRepo.findById(validationId).orElseThrow();
        
        // Verify the aggregate reflects the GitHub link
        assertEquals(
            expectedUrl, 
            aggregate.getGithubIssueUrl(),
            "ValidationAggregate should store the GitHub URL"
        );
    }

    @Test
    void testReportDefect_RequiresNonBlankUrl() {
        // Arrange
        String validationId = "VW-456";

        // Act & Assert
        // The implementation should reject blank URLs or handle them gracefully,
        // but based on the defect, the URL is expected to be present.
        // We verify the orchestrator doesn't crash and validates input.
        assertThrows(
            IllegalArgumentException.class,
            () -> orchestrator.reportDefect(validationId, "DEF-104", "  "),
            "Should throw IllegalArgumentException for blank URL"
        );
    }
}
