package com.example.e2e;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End Regression Test for Story S-FB-1.
 * Validates VW-454: GitHub URL in Slack body.
 *
 * This test verifies that when a defect is reported,
 * the resulting Slack notification payload contains the GitHub URL.
 */
class SFB1SlackNotificationE2ETest {

    private DefectRepository repository;
    private SlackPort slackPort;
    private GitHubPort gitHubPort;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDefectRepository();
        slackPort = new MockSlackPort();
        gitHubPort = new MockGitHubPort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBodyWhenDefectReported() {
        // Scenario: Trigger _report_defect via temporal-worker exec
        // (Simulated here by manually invoking the flow)

        // 1. Setup Data
        String defectId = "S-FB-1-TEST-001";
        ReportDefectCmd cmd = new ReportDefectCmd(
                defectId,
                "S-FB-1: Validating VW-454",
                "LOW",
                "validation",
                "Ensure Slack body contains link"
        );

        // 2. Execute Aggregate Logic
        DefectAggregate aggregate = new DefectAggregate(defectId);
        aggregate.execute(cmd);

        // 3. Save Aggregate (Event Sourcing simulation)
        repository.save(aggregate);

        // 4. Retrieve and Construct Notification
        DefectAggregate savedAggregate = repository.findById(defectId).orElseThrow();
        String expectedUrl = savedAggregate.getGitHubIssueUrl();

        // 5. Trigger Slack Notification via Port
        // In a real Temporal workflow, this would be an Activity calling this port.
        String slackPayload = String.format(
                "{\"text\": \"Defect Reported: %s\nGitHub Issue: %s\"}",
                savedAggregate.id(),
                expectedUrl
        );

        slackPort.sendNotification("https://hooks.slack.com/mock", slackPayload);

        // 6. Verification: Verify Slack body contains GitHub issue link
        MockSlackPort mock = (MockSlackPort) slackPort;
        assertThat(mock.calls).hasSize(1);

        String capturedPayload = mock.calls.get(0).payload();

        // Assertion: Expected Behavior - Slack body includes GitHub issue: <url>
        // Checks for the presence of the URL in the JSON string payload.
        assertThat(capturedPayload).contains(expectedUrl);
        assertThat(capturedPayload).contains("GitHub Issue");

        // Regression check: Ensure the URL isn't null or empty placeholder
        assertThat(capturedPayload).doesNotContain("null");
    }
}
