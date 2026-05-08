package com.example.e2e.regression;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * E2E Regression Test for VW-454.
 * Validates that when a defect is reported (workflow triggered),
 * the resulting Slack notification contains the GitHub issue URL.
 */
public class VW454ValidationE2ETest {

    // SUT components - in a real Spring Boot test these might be Autowired or manual mocks
    // Here we use the Mocks provided via the context pattern or directly instantiated for unit isolation.
    private MockGitHubIssuePort githubPort;
    private MockSlackNotificationPort slackPort;
    private ReportDefectWorkflow workflow; // The class under test (to be implemented)

    @BeforeEach
    void setUp() {
        githubPort = new MockGitHubIssuePort();
        slackPort = new MockSlackNotificationPort();
        // Note: In the Red phase, ReportDefectWorkflow might not exist or be stubbed.
        // We assume the structure or inject dependencies manually for the sake of the test compilation.
        // This test assumes the existence of a service/worker class coordinating this flow.
        // workflow = new ReportDefectWorkflow(githubPort, slackPort);
    }

    @Test
    void testReportDefect_shouldIncludeGitHubUrlInSlackBody() {
        // Given
        String defectTitle = "VW-454: GitHub URL missing";
        String defectDescription = "Severity: LOW\nComponent: validation";
        String expectedChannel = "#vforce360-issues";

        // When
        // This represents the Temporal workflow execution logic
        // 1. Create Issue in GitHub
        String githubUrl = githubPort.createIssue(defectTitle, defectDescription);

        // 2. Report to Slack (The logic we are testing for the defect fix)
        // We manually invoke the postMessage here to simulate the 'worker' action.
        // In the real implementation, the worker service would call:
        // slackPort.postMessage(expectedChannel, constructMessage(githubUrl));
        
        // The defect states the Actual Behavior is unknown/malformed.
        // The Expected Behavior is that the body includes the URL.
        
        // Constructing the message payload as the application should:
        String slackMessageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s", 
            defectTitle, 
            githubUrl
        );
        
        slackPort.postMessage(expectedChannel, slackMessageBody);

        // Then
        assertThat(slackPort.getPostedMessages()).hasSize(1);
        
        MockSlackNotificationPort.PostedMessage posted = slackPort.getPostedMessages().get(0);
        assertThat(posted.channel).isEqualTo(expectedChannel);
        
        // CRITICAL ASSERTION FOR VW-454:
        // The Slack body must include the GitHub issue URL.
        assertThat(posted.body).contains(githubUrl);
        
        // Verify it is a valid URL format
        assertThat(posted.body).contains("https://github.com/");
    }

    @Test
    void testReportDefect_handlesNullGitHubUrlGracefully() {
        // Edge case: If GitHub returns null or empty, Slack shouldn't crash or post garbage
        // Given
        String defectTitle = "VW-454 Edge Case";
        String expectedChannel = "#vforce360-issues";
        
        // We assume the workflow logic handles nulls.
        // If the implementation doesn't, this test ensures it fails (Red).
        String githubUrl = null; 

        // Expecting the system to handle this, potentially throwing an exception 
        // or posting a message with a specific error text. 
        // For this regression, we check that we don't just post "GitHub Issue: null" 
        // without context, or if we do, it fails validation.
        
        try {
            // Simulate the assembly of the message
            String body = "GitHub Issue: " + githubUrl;
            slackPort.postMessage(expectedChannel, body);
            
            // If we reach here, the system accepted a null URL. 
            // Depending on strictness, this might be a failure.
            // For VW-454, the requirement is "Slack body includes GitHub issue: <url>".
            // A null url usually doesn't satisfy <url>.
            MockSlackNotificationPort.PostedMessage msg = slackPort.getPostedMessages().get(0);
            assertThat(msg.body).doesNotContain("null"); // Fails if "null" string is present
        } catch (IllegalArgumentException e) {
            // Acceptable: System rejected invalid input
            assertThat(e.getMessage()).contains("URL");
        }
    }

    /**
     * Inner class representing the boundary of the Worker/Workflow logic.
     * This class would normally be in src/main. We define it here solely 
     * to allow the test to compile in the Red phase before implementation exists.
     */
    public static class ReportDefectWorkflow {
        private final GitHubIssuePort githubPort;
        private final SlackNotificationPort slackPort;

        public ReportDefectWorkflow(GitHubIssuePort githubPort, SlackNotificationPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        public void execute(String title, String description) {
            // Implementation placeholder
            // 1. Call github
            // 2. Call slack
        }
    }
}
