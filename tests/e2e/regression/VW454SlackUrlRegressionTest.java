package e2e.regression;

import com.example.domain.shared.DomainEvent;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Regression Test for Story S-FB-1 / Defect VW-454.
 * 
 * Validates that when a defect is reported via the temporal-worker flow,
 * the resulting Slack body includes the GitHub URL.
 */
public class VW454SlackUrlRegressionTest {

    @Test
    void verifyDefectReportingWorkflowGeneratesCorrectSlackBody() {
        // Setup: Simulate Temporal worker environment components
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
        String defectId = "VW-454";
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        
        // Step 1: Trigger _report_defect via temporal-worker exec
        // (Simulated by creating the command and processing aggregate)
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId, 
            "Validating VW-454 - GitHub URL in Slack body",
            "Defect reported by user. Severity: LOW",
            githubUrl,
            Map.of("severity", "LOW", "component", "validation")
        );

        ValidationAggregate aggregate = new ValidationAggregate(defectId);
        List<DomainEvent> events = aggregate.execute(cmd);

        // Step 2: Verify Slack body contains GitHub issue link
        assertFalse(events.isEmpty(), "Expected events to be generated");
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        String slackBody = event.slackBody();

        assertNotNull(slackBody, "Slack body cannot be null");
        
        // VW-454 Core Validation
        assertTrue(slackBody.contains(githubUrl), 
            "Regression Check: Slack body must include the specific GitHub issue URL: " + githubUrl);
        
        assertTrue(slackBody.contains("GitHub Issue:"), 
            "Slack body should label the URL clearly");

        // Additional: Verify the mock port would accept this payload
        assertDoesNotThrow(() -> mockSlack.send(slackBody));
        assertEquals(slackBody, mockSlack.getLastMessage());
    }
}
