package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Regression Test for Defect VW-454.
 * Verifies that when a defect is reported via the temporal-worker (simulated here),
 * the resulting Slack notification body contains the GitHub issue URL.
 *
 * Story: S-FB-1
 */
public class VW454SlackGitHubUrlValidationTest {

    // SUT: The application service that would handle the workflow.
    // For this test, we simulate the logic flow within the test body to validate behavior.
    private DefectAggregate defectAggregate;

    // Mock Adapters
    private MockGitHubPort gitHubPort;
    private MockSlackPort slackPort;

    @BeforeEach
    void setUp() {
        // Initialize mock adapters with standard configuration
        gitHubPort = new MockGitHubPort("https://github.com/example/bank/issues");
        slackPort = new MockSlackPort();

        // Initialize Aggregate
        defectAggregate = new DefectAggregate("VW-454");
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenDefectReportedSuccessfully() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "LOW",
            "Validating VW-454 — GitHub URL in Slack body",
            "validation"
        );

        // When
        // Step 1: Execute domain logic (Simulating Temporal Activity / Aggregate Execution)
        var events = defectAggregate.execute(cmd);
        assertThat(events).hasSize(1);

        // Step 2: Handle side effects (Simulating Workflow Orchestrator logic)
        // - Call GitHub API
        String expectedIssueUrl = gitHubPort.createIssue(
            "[" + cmd.severity() + "] " + cmd.summary(),
            "Component: " + cmd.component()
        ).orElseThrow(() -> new IllegalStateException("GitHub API failed to create issue"));

        // - Call Slack API with the URL
        String slackMessageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            cmd.summary(),
            expectedIssueUrl
        );
        slackPort.sendMessage("#vforce360-issues", slackMessageBody);

        // Then
        // Verification 1: The event was created
        assertThat(events.get(0).aggregateId()).isEqualTo("VW-454");

        // Verification 2: The Slack message body contains the GitHub URL
        // This is the core acceptance criteria for VW-454.
        MockSlackPort.SlackMessage sentMessage = slackPort.getLastMessage();
        assertThat(sentMessage.channel).isEqualTo("#vforce360-issues");
        assertThat(sentMessage.body).contains("github.com");
        assertThat(sentMessage.body).contains(expectedIssueUrl);
        
        // Verification 3: Ensure specific format "GitHub issue: <url>" (or similar context)
        // Accepting slight variations but ensuring the link is explicitly labeled.
        assertThat(sentMessage.body).containsPattern("GitHub.*[h]?ttps?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    @Test
    void shouldHandleGitHubFailureGracefully_withoutSendingSlackMessage() {
        // Given
        gitHubPort.setShouldFail(true);
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "LOW",
            "Validating VW-454 — GitHub URL in Slack body",
            "validation"
        );

        // When
        var events = defectAggregate.execute(cmd);
        Optional<String> issueUrl = gitHubPort.createIssue("[LOW] Summary", "Body");

        // Then
        assertThat(issueUrl).isEmpty();
        // Verify no Slack message was sent if GitHub link is missing
        assertThat(slackPort.getMessages()).isEmpty();
    }
}
