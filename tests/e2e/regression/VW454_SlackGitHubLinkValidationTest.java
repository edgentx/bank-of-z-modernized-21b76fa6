package e2e.regression;

import com.example.domain.reporting.model.DefectReportedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.MockNotificationFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for defect VW-454.
 * Validates that the GitHub issue URL is present in the formatted Slack body.
 */
public class VW454_SlackGitHubLinkValidationTest {

    @Test
    @DisplayName("VW-454: Verify Slack body contains GitHub URL")
    public void testSlackBodyContainsGitHubUrl() {
        // 1. Setup: Create a mock event containing a GitHub URL
        String expectedUrl = "https://github.com/bank-of-z/legacy-modernization/issues/454";
        String defectId = "VW-454";
        
        DefectReportedEvent event = new DefectReportedEvent(
            "agg-123",
            defectId,
            "Validating VW-454",
            Map.of("githubUrl", expectedUrl, "severity", "LOW"),
            Instant.now()
        );

        // 2. Act: Use the Mock Adapter (or real Formatter) to generate the body
        // Note: In a real integration test, this might wire up the Spring Context.
        // Here we test the contract logic directly via the mock/implementation class.
        MockNotificationFormatter formatter = new MockNotificationFormatter();
        String slackBody = formatter.formatDefectForSlack(event);

        // 3. Assert: The body must include the URL
        assertNotNull(slackBody, "Slack body should not be null");
        
        // This assertion implements the 'Expected Behavior' from the defect report:
        // "Slack body includes GitHub issue: <url>"
        assertTrue(
            slackBody.contains(expectedUrl),
            "Regression detected (VW-454): Slack body must contain the GitHub issue URL. " +
            "Expected [" + expectedUrl + "] but got: [" + slackBody + "]"
        );
        
        // Additional sanity check to ensure it's not just empty or noise
        assertFalse(slackBody.isBlank(), "Slack body cannot be blank");
    }
}
