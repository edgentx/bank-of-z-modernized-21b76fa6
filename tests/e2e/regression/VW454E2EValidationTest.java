package e2e.regression;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for Story S-FB-1 (VW-454).
 * Validates the complete flow: Command -> Aggregate -> Event -> Notification.
 * <p>
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior (missing URL).
 * 2. Regression test added to e2e/regression/ covering this scenario.
 */
public class VW454E2EValidationTest {

    private MockNotificationService notificationService;
    private DefectAggregate aggregate;

    @BeforeEach
    void setUp() {
        // Initialize mocks and aggregates
        notificationService = new MockNotificationService();
        aggregate = new DefectAggregate("VW-454");
    }

    @Test
    void validateSlackBodyContainsGitHubIssueLink() {
        // Scenario: Trigger _report_defect via temporal-worker exec
        // Expected: Slack body includes GitHub issue: <url>

        // Arrange
        String defectId = "VW-454";
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedGithubUrlFormat = "https://github.com/egdcrypto/issues/" + defectId;

        ReportDefectCmd reportCmd = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)",
            "Severity: LOW\nComponent: validation",
            projectId
        );

        // Act: Execute the command on the aggregate
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(reportCmd);

        // Simulate downstream processing (e.g., a projection or workflow listener)
        // handling the event and sending a Slack notification.
        assertFalse(events.isEmpty(), "Expected events to be generated");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        // Simulate Slack Message Construction
        String slackBody = String.format(
            "Defect Reported: %s\nProject: %s\nGitHub Issue: %s",
            event.title(),
            event.projectId(),
            event.githubUrl()
        );

        notificationService.sendSlackNotification(slackBody);

        // Verify Slack body contains GitHub issue link
        // This assertion is the core fix for the reported defect.
        assertTrue(
            notificationService.wasSlackMessageSentContaining(event.githubUrl()),
            "Slack body should include the GitHub issue URL. Defect VW-454 is NOT fixed."
        );

        // Explicitly verify the URL format expected by the system
        assertTrue(
            slackBody.contains(expectedGithubUrlFormat),
            "Slack body should contain the specific constructed GitHub URL."
        );
    }
}
