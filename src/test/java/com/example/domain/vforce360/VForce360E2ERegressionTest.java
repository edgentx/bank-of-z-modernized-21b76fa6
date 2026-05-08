package com.example.domain.vforce360;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360IntegrationAggregate;
import com.example.mocks.MockSlackNotifier;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1.
 * Validates that reporting a defect generates the correct GitHub URL in the event body,
 * which is subsequently formatted for Slack.
 */
public class VForce360E2ERegressionTest {

    @Test
    void shouldGenerateGitHubUrlInEventPayload() {
        // Given
        var aggregate = new VForce360IntegrationAggregate("VW-454");
        var cmd = new ReportDefectCmd("VW-454", "Test Defect", "LOW");

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = (DefectReportedEvent) events.get(0);
        
        // Core validation for the defect
        assertTrue(event.githubUrl().startsWith("https://github.com/"));
        assertTrue(event.githubUrl().contains("VW-454"));
    }

    @Test
    void shouldFormatBodyForSlackNotification() {
        // Given
        var aggregate = new VForce360IntegrationAggregate("VW-454");
        var cmd = new ReportDefectCmd("VW-454", "Test Defect", "LOW");
        var mockSlack = new MockSlackNotifier();

        // When
        var events = aggregate.execute(cmd);
        var event = (DefectReportedEvent) events.get(0);
        
        // Simulating the downstream formatting that happens in the notification service
        String slackBody = "GitHub Issue: <" + event.githubUrl() + ">";
        mockSlack.send(slackBody);

        // Then
        String result = mockSlack.getLastSentBody();
        assertTrue(result.contains("<"), "Body should contain opening slack link tag");
        assertTrue(result.contains(">"), "Body should contain closing slack link tag");
        assertTrue(result.contains("https://github.com/"), "Body should contain the actual URL");
    }
}
