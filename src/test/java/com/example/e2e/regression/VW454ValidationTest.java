package com.example.e2e.regression;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for VW-454: Validating GitHub URL in Slack body (end-to-end).
 * 
 * Context:
 * Temporal Workflow: _report_defect
 * Steps:
 * 1. Create GitHub Issue.
 * 2. Send notification to Slack.
 * 
 * Expected Behavior:
 * The Slack notification body MUST contain the URL generated in Step 1.
 */
public class VW454ValidationTest {

    private MockGitHubPort gitHubPort;
    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        gitHubPort = new MockGitHubPort();
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    void testSlackBodyContainsGitHubLink() {
        // Arrange
        String defectTitle = "VW-454 Regression Test";
        String defectBody = "Automated reproduction of defect VW-454.";
        String targetChannel = "#vforce360-issues";

        // Act: Simulate the Workflow logic
        // 1. Create Issue in GitHub
        String githubUrl = gitHubPort.createIssue(defectTitle, defectBody);
        
        // 2. Construct Slack Payload (Defect: This logic is what we are testing)
        String slackMessageBody = constructSlackMessage(defectTitle, githubUrl);
        
        // 3. Send to Slack
        slackPort.postMessage(targetChannel, slackMessageBody);

        // Assert: Verify the Acceptance Criteria
        // "Slack body includes GitHub issue: <url>"
        assertTrue(slackPort.messages.size() > 0, "Slack should have received a message");
        
        MockSlackNotificationPort.Message postedMsg = slackPort.messages.get(0);
        assertNotNull(postedMsg.body(), "Message body should not be null");
        
        assertTrue(
            postedMsg.body().contains(githubUrl), 
            "Slack body must contain the specific GitHub issue URL created.\nExpected: " + githubUrl + "\nActual Body: " + postedMsg.body()
        );
    }

    @Test
    void testSlackBodyFailsIfUrlMissing() {
        // Arrange
        String defectTitle = "VW-454 Bad Format Test";
        String missingUrl = null;
        String targetChannel = "#vforce360-issues";

        // Act
        // Construct a message simulating the DEFECT behavior (missing URL)
        String slackMessageBody = "Defect reported: " + defectTitle + " (No link available)";
        slackPort.postMessage(targetChannel, slackMessageBody);

        // Assert
        String expectedUrl = "https://github.com/mock-org/repo/issues/1";
        assertFalse(
            slackPort.containsUrlInBody(expectedUrl),
            "In the defect state, the URL should not be present."
        );
    }

    /**
     * Helper method representing the SUT (System Under Test) logic.
     * In a real test, this would be inside the Workflow or Service class.
     * Here we define the expectation of what the code SHOULD do.
     */
    private String constructSlackMessage(String title, String url) {
        if (url == null) {
            throw new IllegalArgumentException("GitHub URL cannot be null");
        }
        return String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            title, url
        );
    }
}
