package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TDD Test Suite for VW-454.
 * 
 * RED PHASE: These tests are written to FAIL initially.
 * They expose the bug where the Slack body does not contain the GitHub URL.
 */
class ValidationAggregateTest {

    private MockSlackNotificationPort mockSlack;
    private ValidationAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
        aggregate = new ValidationAggregate("test-validation-id", mockSlack);
    }

    @Test
    void shouldReportDefectSuccessfully() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEFECT-101",
            "Critical calculation error",
            "The system fails to calculate interest correctly",
            "HIGH",
            Map.of("component", "interest-engine")
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertThat(aggregate.isDefectReported()).isTrue();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(DefectReportedEvent.class);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody() {
        // This test verifies the specific bug reported in VW-454.
        // It expects the Slack body to contain the URL, but the current implementation
        // (in ValidationAggregate) is intentionally bugged and will fail this test.

        // Given
        String defectId = "VW-454";
        String expectedUrlFragment = "github.com/issues/" + defectId;
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Slack body missing URL",
            "The link to the issue is not showing up in Slack",
            "LOW",
            Map.of()
        );

        // When
        aggregate.execute(cmd);
        String actualSlackBody = mockSlack.getLastPayload();

        // Then
        // TDD RED PHASE:
        // The current implementation of ValidationAggregate.reportDefect() creates a body:
        // "Defect Reported: ... \nSeverity: ..." which DOES NOT contain the URL.
        // This assertion will fail.
        assertThat(actualSlackBody)
            .withFailMessage(
                "Expected Slack body to contain GitHub URL fragment '%s', but got: %s", 
                expectedUrlFragment, actualSlackBody
            )
            .contains(expectedUrlFragment);
    }

    @Test
    void shouldPersistUrlInDomainEvent() {
        // Even if the Slack notification is buggy, the domain event should probably hold the link.
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Title",
            "Desc",
            "LOW",
            Map.of()
        );

        // When
        var events = aggregate.execute(cmd);
        var event = (DefectReportedEvent) events.get(0);

        // Then
        assertThat(event.githubIssueUrl()).isNotEmpty();
        assertThat(event.githubIssueUrl()).contains("github.com");
    }
}
