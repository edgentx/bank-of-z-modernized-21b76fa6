package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TDD Red Phase for Story S-FB-1 / Defect VW-454.
 * Validating that the Slack body contains the GitHub URL.
 */
class DefectAggregateVW454Test {

    private static final String DEFECT_ID = "D-454";
    private static final String EXPECTED_GITHUB_URL = "https://github.com/bank-of-z/vforce360/issues/454";

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenReportingDefect() {
        // Given
        MockGitHubIssuePort gitHub = new MockGitHubIssuePort();
        gitHub.setMockUrl(EXPECTED_GITHUB_URL);

        MockSlackNotificationPort slack = new MockSlackNotificationPort();

        DefectAggregate aggregate = new DefectAggregate(DEFECT_ID, gitHub, slack);
        ReportDefectCmd cmd = new ReportDefectCmd(
                DEFECT_ID,
                "VW-454: GitHub URL in Slack body",
                "Defect description...",
                "LOW"
        );

        // When
        List events = aggregate.execute(cmd);

        // Then
        // 1. Verify Command was processed successfully
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(DefectReportedEvent.class);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertThat(event.githubIssueUrl()).isEqualTo(EXPECTED_GITHUB_URL);

        // 2. Verify Slack received the message (Expected Behavior)
        // "Slack body includes GitHub issue: <url>"
        String actualSlackBody = slack.getLastBody();
        assertThat(actualSlackBody).isNotNull();
        assertThat(actualSlackBody).contains(EXPECTED_GITHUB_URL);

        // 3. Verify it was sent to the correct channel
        assertThat(slack.getLastChannel()).isEqualTo("#vforce360-issues");
    }

    @Test
    void shouldIncludePlaceholderInSlackBodyIfGitHubFails() {
        // Given
        MockGitHubIssuePort gitHub = new MockGitHubIssuePort();
        gitHub.setShouldFail(true);

        MockSlackNotificationPort slack = new MockSlackNotificationPort();

        DefectAggregate aggregate = new DefectAggregate(DEFECT_ID, gitHub, slack);
        ReportDefectCmd cmd = new ReportDefectCmd(
                DEFECT_ID,
                "VW-454: GitHub URL in Slack body",
                "Defect description...",
                "LOW"
        );

        // When
        aggregate.execute(cmd);

        // Then
        // Even if GitHub fails, the Slack body should reflect the status/URL (or lack thereof)
        String actualSlackBody = slack.getLastBody();
        assertThat(actualSlackBody).contains("[GitHub URL not generated]");
    }

    @Test
    void shouldThrowExceptionIfTitleIsMissing() {
        // Given
        MockGitHubIssuePort gitHub = new MockGitHubIssuePort();
        MockSlackNotificationPort slack = new MockSlackNotificationPort();
        DefectAggregate aggregate = new DefectAggregate(DEFECT_ID, gitHub, slack);

        ReportDefectCmd cmd = new ReportDefectCmd(DEFECT_ID, "", "Desc", "HIGH");

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
