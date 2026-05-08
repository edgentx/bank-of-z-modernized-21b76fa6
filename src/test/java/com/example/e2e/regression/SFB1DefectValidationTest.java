package com.example.e2e.regression;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Regression Test for Story S-FB-1.
 * Validating VW-454 — GitHub URL in Slack body (end-to-end).
 * 
 * Tests that the VForce360 defect reporting workflow includes
 * the GitHub issue URL in the Slack notification body.
 */
class SFB1DefectValidationTest {

    private MockSlackNotificationPort mockSlack;
    private VForce360Aggregate aggregate;
    private static final String DEFECT_ID = "VW-454";
    private static final String STORY_ID = "S-FB-1";
    private static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackNotificationPort();
        aggregate = new VForce360Aggregate(DEFECT_ID, mockSlack);
    }

    @Test
    void shouldIncludeGitHubIssueLinkInSlackBody() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            DEFECT_ID,
            "Fix: Validating VW-454",
            "LOW",
            PROJECT_ID,
            STORY_ID,
            "Trigger _report_defect..."
        );

        // When
        aggregate.execute(cmd);

        // Then
        String slackBody = mockSlack.getLatestPayload();
        
        // Validation: Body must contain the specific GitHub Issue URL format
        // Expected: https://github.com/example-org/bank-of-z-modernization/issues/S-FB-1
        String expectedUrl = "https://github.com/example-org/bank-of-z-modernization/issues/" + STORY_ID;
        
        assertThat(slackBody)
            .as("Slack body should contain the GitHub issue URL")
            .contains(expectedUrl);
            
        assertThat(slackBody)
            .as("Slack body should contain the Story ID for reference")
            .contains(STORY_ID);
    }

    @Test
    void shouldFailIfCommandIsUnknown() {
        // Given: An invalid command (e.g. a plain record implementing Command)
        Object invalidCmd = new Object() implements com.example.domain.shared.Command {};

        // When / Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute((com.example.domain.shared.Command) invalidCmd);
        });
    }

    @Test
    void shouldEmitDefectReportedEvent() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            DEFECT_ID, "Test Defect", "HIGH", PROJECT_ID, STORY_ID, "Steps"
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertThat(events).hasSize(1);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        assertThat(event.aggregateId()).isEqualTo(DEFECT_ID);
        assertThat(event.storyId()).isEqualTo(STORY_ID);
    }
}
