package com.example.domain.notification.validation;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for Story S-FB-1.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * Component: validation
 * 
 * Scenario: Triggering report_defect via temporal-worker exec should result
 * in a Slack message body that contains the GitHub issue link.
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 * Actual Behavior: (Defect) Link line is missing/malformed.
 */
@DisplayName("VW-454: Validation of Slack Notification Body for GitHub URLs")
class Vw454SlackValidationTest {

    private MockSlackPort slackMock;
    private MockGitHubPort githubMock;
    private ReportDefectWorkflow workflow; // Class under test (assumed name)

    @BeforeEach
    void setUp() {
        slackMock = new MockSlackPort();
        githubMock = new MockGitHubPort();
        
        // Inject mocks into the workflow/command handler
        // Note: In TDD Red phase, this class might not exist yet or be empty.
        workflow = new ReportDefectWorkflow(slackMock, githubMock);
    }

    @Test
    @DisplayName("When report_defect is triggered, Slack body must contain the GitHub URL")
    void testSlackBodyContainsGitHubUrl() throws Exception {
        // Arrange
        String issueId = "VW-454";
        String expectedUrl = githubMock.generateIssueUrl(issueId);
        String expectedChannel = "#vforce360-issues";

        // Act
        // This simulates the temporal-worker exec triggering the defect report
        workflow.execute(new ReportDefectCommand(issueId, "Defect found in validation"));

        // Allow for async processing if necessary, though mocks are sync
        
        // Assert
        // 1. Verify message was sent to the correct channel
        assertTrue(
            slackMock.hasReceivedMessageForChannel(expectedChannel),
            "Slack should receive a message for channel: " + expectedChannel
        );

        // 2. Verify the message body contains the specific URL (Acceptance Criteria)
        String actualBody = slackMock.getLastMessageBody();
        
        assertNotNull(actualBody, "Slack body should not be null");
        
        // This assertion represents the "Expected Behavior" vs "Actual Behavior" fix.
        // It will FAIL (Red) until the implementation correctly appends the URL.
        assertTrue(
            actualBody.contains(expectedUrl),
            "Slack body must contain GitHub issue URL: " + expectedUrl + ".\nActual body was: " + actualBody
        );
    }

    @Test
    @DisplayName("GitHub URL format must match standard pattern (https://github.com/org/repo/issues/VW-454)")
    void testGitHubUrlFormat() throws Exception {
        // Arrange
        String issueId = "VW-454";

        // Act
        workflow.execute(new ReportDefectCommand(issueId, "Check link format"));

        // Assert
        String actualBody = slackMock.getLastMessageBody();
        assertTrue(actualBody.contains("https://github.com/"), "URL should use https protocol and github domain");
        assertTrue(actualBody.contains("issues/"), "URL should point to issues section");
    }

    @Test
    @DisplayName("If GitHub URL is missing, test should fail (Regression Guard)")
    void testRegressionGuardMissingLink() throws Exception {
        // Arrange
        String issueId = "VW-454";

        // Act
        workflow.execute(new ReportDefectCommand(issueId, "Missing link scenario"));

        // Assert
        String actualBody = slackMock.getLastMessageBody();
        
        // Explicit check for the defect described: "About to find out — checking #vforce360-issues for the link line"
        // If the body is just plain text without the link, this fails.
        assertFalse(
            actualBody.equals("About to find out — checking #vforce360-issues for the link line"),
            "The defect behavior should be fixed: body cannot be the placeholder text."
        );
        
        // And it must have the link.
        assertTrue(
            actualBody.contains("http"), 
            "Regression check: Body must contain an http link."
        );
    }
}

/**
 * Command object representing the input to the workflow.
 */
class ReportDefectCommand {
    private final String issueId;
    private final String description;

    public ReportDefectCommand(String issueId, String description) {
        this.issueId = issueId;
        this.description = description;
    }

    public String getIssueId() { return issueId; }
    public String getDescription() { return description; }
}

/**
 * The class under test.
 * In the actual Red phase, this file would be empty or throw UnsupportedOperationException,
 * causing the tests above to fail.
 * 
 * Since I cannot create the 'real' implementation files, I am defining the shell here 
 * to allow the test structure to compile/verify logic structure, 
 * but the tests are designed to fail against the currently broken logic described in the defect.
 */
class ReportDefectWorkflow {
    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public ReportDefectWorkflow(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    public void execute(ReportDefectCommand cmd) throws ExecutionException, InterruptedException {
        // This implementation reflects the ACTUAL BEHAVIOR (Defect) or Empty.
        // For Red phase, we ensure this logic fails the Test Assertions above.
        // Example of Broken Logic:
        // slackPort.sendMessage("#vforce360-issues", "About to find out — checking #vforce360-issues for the link line").get();
        
        // Note: The prompt asks for the Test Files. The logic to make them Green would go in src/main.
        // Leaving this shell for compilation context.
        throw new UnsupportedOperationException("Implementation missing - Test Red Phase");
    }
}
