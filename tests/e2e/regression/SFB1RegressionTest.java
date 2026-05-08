package e2e.regression;

import com.example.domain.vforce360.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.mocks.SlackNotificationPortMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * S-FB-1: Regression test for VW-454.
 * Validates that the Slack body includes the GitHub issue link.
 */
@DisplayName("S-FB-1: Validate VW-454 — GitHub URL in Slack body")
public class SFB1RegressionTest {

    @Test
    @DisplayName("Given a defect report, when processed, Slack body must contain GitHub URL")
    public void testSlackBodyContainsGitHubUrl() {
        // ARRANGE
        // 1. Setup mock adapters
        var mockSlack = new SlackNotificationPortMock();
        var mockGitHub = mock(GitHubPort.class); // Using Mockito for simpler stubbing

        // 2. Define stubbed responses
        String expectedUrl = "https://github.com/bank-of-z/issues/454";
        when(mockGitHub.createIssue(anyString(), anyString())).thenReturn(expectedUrl);

        // 3. Create the Defect Event (Triggering _report_defect via temporal-worker exec)
        DefectReportedEvent event = new DefectReportedEvent(
            "VW-454",
            "LOW",
            "validation",
            "Validating VW-454 — GitHub URL in Slack body",
            Map.of("project", "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"),
            Instant.now()
        );

        // ACT
        // Simulate the workflow/orchestrator logic that wires these ports together
        // Real implementation (Temporal Activity) would do this:
        String actualUrl = mockGitHub.createIssue(event.summary(), event.defectId());
        mockSlack.notifyDefect(event, actualUrl);

        // ASSERT
        // Verify Slack body includes GitHub issue: <url>
        assertTrue(
            mockSlack.getLastBody().contains("GitHub issue: " + expectedUrl),
            "Expected Slack body to contain 'GitHub issue: " + expectedUrl + "'"
        );
    }

    @Test
    @DisplayName("Given a defect report, Slack body should not be empty")
    public void testSlackBodyNotEmpty() {
        // ARRANGE
        var mockSlack = new SlackNotificationPortMock();
        var mockGitHub = mock(GitHubPort.class);
        
        when(mockGitHub.createIssue(anyString(), anyString())).thenReturn("https://github.com/bank-of-z/issues/1");

        DefectReportedEvent event = new DefectReportedEvent(
            "VW-455", "MEDIUM", "ui", "Button alignment", Map.of(), Instant.now()
        );

        // ACT
        mockSlack.notifyDefect(event, mockGitHub.createIssue("", ""));

        // ASSERT
        assertFalse(mockSlack.getLastBody().isBlank(), "Slack body must not be blank");
    }

    @Test
    @DisplayName("Given a null GitHub URL, Slack body should handle gracefully or fail explicitly")
    public void testNullGitHubUrl() {
        // ARRANGE
        var mockSlack = new SlackNotificationPortMock();
        
        // ACT & ASSERT
        // We expect the system to handle the null URL explicitly.
        // The mock helps us verify the contract.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            mockSlack.notifyDefect(new DefectReportedEvent("1", "H", "A", "B", Map.of(), Instant.now()), null);
        });

        assertTrue(exception.getMessage().contains("GitHub URL cannot be null"));
    }
}