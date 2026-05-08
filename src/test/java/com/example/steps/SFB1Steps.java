package com.example.steps;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockGitHubMetadataPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating GitHub URL in Slack body.
 * Testing the integration between Temporal defect reporting and Slack notifications.
 */
public class SFB1Steps {

    // We would typically inject the Workflow implementation here.
    // For this red-phase test, we will simulate the workflow logic directly in the step.
    
    @Autowired
    private SlackNotificationPort slackNotificationPort; // This will be the Mock

    @Autowired
    private MockGitHubMetadataPort mockGitHubMetadataPort;

    private ReportDefectCmd currentCommand;
    private Exception capturedException;

    @Given("the defect {string} has a GitHub issue URL {string}")
    public void the_defect_has_a_github_issue_url(String defectId, String url) {
        mockGitHubMetadataPort.mockUrlForDefect(defectId, url);
    }

    @When("the defect {string} is reported via temporal-worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec(String defectId) {
        // 1. Prepare the command
        currentCommand = new ReportDefectCmd(defectId, "VForce360 PM diagnostic failure");
        
        // 2. Simulate the Workflow/Activity logic that needs to be fixed
        // This logic currently lives in the temporal worker (which we are fixing)
        try {
            String githubUrl = mockGitHubMetadataPort.getIssueUrl(currentCommand.defectId());
            
            // THIS IS THE BUGGY CODE PATH (Expected Actual Behavior before fix)
            // The defect report implies the URL is missing from the body.
            // We simulate sending the report here.
            String slackBody = "Defect Reported: " + currentCommand.defectId() 
                             + "\nTitle: " + currentCommand.title();
                             // Bug: Missing GitHub URL injection here

            ((MockSlackNotificationPort) slackNotificationPort).sendDefectReport(slackBody);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        assertNotNull(slackNotificationPort, "SlackNotificationPort should be mocked");
        
        MockSlackNotificationPort mockSlack = (MockSlackNotificationPort) slackNotificationPort;
        assertEquals(1, mockSlack.sentMessages.size(), "Exactly one message should be sent");

        String sentBody = mockSlack.sentMessages.get(0);
        
        // ASSERTION: Verify the URL is present
        String expectedUrl = mockGitHubMetadataPort.getIssueUrl(currentCommand.defectId());
        
        // This assertion will FAIL in the Red phase because the URL is not appended to the body in the @When step above.
        assertTrue(sentBody.contains(expectedUrl), 
            "Slack body must contain GitHub URL. Expected: " + expectedUrl + " in body: [" + sentBody + "]");
    }

    @Then("the Slack body should not be empty")
    public void the_slack_body_should_not_be_empty() {
        MockSlackNotificationPort mockSlack = (MockSlackNotificationPort) slackNotificationPort;
        assertFalse(mockSlack.sentMessages.isEmpty(), "Slack messages should not be empty");
        assertFalse(mockSlack.sentMessages.get(0).isBlank(), "Slack body content should not be blank");
    }
}
