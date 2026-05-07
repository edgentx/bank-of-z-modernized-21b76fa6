package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.SlackNotifierPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test for VW-454.
 * 
 * Context: Defect reported by user regarding GitHub URL missing in Slack body.
 * Reproduction: Trigger _report_defect, verify body contains link.
 * 
 * This test simulates the end-to-end flow of reporting a defect and ensuring
 * the resulting payload (which would be sent to Slack) contains the required URL.
 */
class VW454SlackBodyValidationTest {

    private MockSlackNotifier mockSlackNotifier;
    // In a real Spring app, we might inject a service that wraps the aggregate and port.
    // For this unit-level regression test, we manually wire the dependencies to verify behavior.

    @BeforeEach
    void setUp() {
        mockSlackNotifier = new MockSlackNotifier();
    }

    @Test
    void shouldContainGitHubUrlInSlackBodyWhenDefectReported() {
        // Given
        String defectId = "VW-454";
        String expectedUrlSuffix = "VW-454";
        
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "Validating VW-454 — GitHub URL in Slack body", 
            "Slack body includes GitHub issue: <url>", 
            Map.of("severity", "LOW", "component", "validation")
        );

        // When - Executing the command (Temporal Workflow Trigger Simulation)
        var events = aggregate.execute(cmd);
        assertThat(events).hasSize(1);
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);

        // And When - Notifying Slack via port
        mockSlackNotifier.notify(event);

        // Then - Verify Expected Behavior: "Slack body includes GitHub issue: <url>"
        // We verify the event payload passed to the Slack adapter contains the URL.
        assertThat(mockSlackNotifier.getCapturedEvents()).hasSize(1);
        
        DefectReportedEvent payloadSentToSlack = mockSlackNotifier.getCapturedEvents().get(0);
        
        // Critical assertion for VW-454
        assertThat(payloadSentToSlack.githubIssueUrl())
            .isNotBlank()
            .contains(expectedUrlSuffix)
            .startsWith("https://github.com/");
            
        // Additional verification of the text content
        assertThat(payloadSentToSlack.title()).contains("Validating VW-454");
    }

    @Test
    void shouldRegressionValidateUrlStructure() {
        // Given a scenario where metadata is missing but ID is present
        DefectAggregate aggregate = new DefectAggregate("S-FB-1");
        ReportDefectCmd cmd = new ReportDefectCmd("S-FB-1", "Fix", "Desc", Map.of());

        // When
        var events = aggregate.execute(cmd);
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        mockSlackNotifier.notify(event);

        // Then
        assertThat(mockSlackNotifier.lastNotificationContainsGithubUrl()).isTrue();
        // Ensure it's not just a string, but a valid looking URL
        assertThat(mockSlackNotifier.getCapturedEvents().get(0).githubIssueUrl())
            .matches("https://github.com/example/issues/S-FB-1");
    }
}
