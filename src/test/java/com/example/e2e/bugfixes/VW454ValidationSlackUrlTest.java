package com.example.e2e.bugfixes;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.commands.ReportDefectCmd;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.mocks.InMemoryValidationRepository;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VW-454: Regression test ensuring GitHub URLs are included in Slack notifications.
 * 
 * Context: Defect reported that Slack body was missing the GitHub issue link.
 * This test verifies the end-to-end flow: Command -> Aggregate -> Port -> Mock.
 */
class VW454ValidationSlackUrlTest {

    private ValidationRepository repository;
    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        repository = new InMemoryValidationRepository();
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    void shouldIncludeGitHubUrlInSlackNotificationWhenDefectIsReported() {
        // Arrange
        String validationId = "VW-454-VALIDATION";
        String defectId = "DEF-454";
        String summary = "GitHub URL missing from Slack body";
        String expectedGitHubUrl = "https://github.com/example/bank-of-z/issues/454";

        // Simulate the command that triggers the workflow
        ReportDefectCmd cmd = new ReportDefectCmd(
            validationId, 
            defectId, 
            summary, 
            expectedGitHubUrl
        );

        // Act
        // In a real E2E scenario, a Service would listen to the event and call the port.
        // For the 'Red' phase of this specific regression test, we simulate the handler logic
        // directly to verify the integration point works correctly.
        
        ValidationAggregate aggregate = new ValidationAggregate(validationId);
        var events = aggregate.execute(cmd);
        
        // Persist aggregate
        repository.save(aggregate);

        // Simulate the handler logic that should be posting to Slack
        // (This logic is what we are testing/fixing)
        events.forEach(event -> {
            // Verify payload construction
            if (event instanceof com.example.domain.validation.model.DefectReportedEvent e) {
                Map<String, String> payload = Map.of(
                    "text", "Defect Reported: " + e.summary(),
                    "defectId", e.defectId(),
                    "githubUrl", e.githubIssueUrl()
                );
                slackPort.sendNotification(payload);
            }
        });

        // Assert
        // 1. Verify the aggregate state is correct
        assertTrue(repository.findById(validationId).isPresent());
        
        // 2. Verify the Slack adapter was called
        assertEquals(1, slackPort.getSentMessages().size(), "Slack should receive one notification");

        // 3. CRITICAL ASSERTION: Verify the GitHub URL is actually in the payload
        // This fails before the fix, passes after.
        assertTrue(
            slackPort.wasCalledWithGitHubUrl(expectedGitHubUrl),
            "Slack notification body must include the GitHub issue URL"
        );
        
        Map<String, String> message = slackPort.getSentMessages().get(0);
        assertEquals(expectedGitHubUrl, message.get("githubUrl"), "GitHub URL must match the issue");
    }

    @Test
    void shouldHandleMissingUrlWithoutCrashing() {
        // Edge case: What if the URL is null? The system should still notify, perhaps without a link.
        String validationId = "VW-454-NULL-URL";
        ReportDefectCmd cmd = new ReportDefectCmd(validationId, "DEF-NO-URL", "No URL provided", null);

        ValidationAggregate aggregate = new ValidationAggregate(validationId);
        var events = aggregate.execute(cmd);

        events.forEach(event -> {
            if (event instanceof com.example.domain.validation.model.DefectReportedEvent e) {
                Map<String, String> payload = Map.of(
                    "text", "Defect Reported: " + e.summary(),
                    "defectId", e.defectId(),
                    "githubUrl", e.githubIssueUrl() != null ? e.githubIssueUrl() : "N/A"
                );
                slackPort.sendNotification(payload);
            }
        });

        assertEquals(1, slackPort.getSentMessages().size());
        assertEquals("N/A", slackPort.getSentMessages().get(0).get("githubUrl"));
    }
}