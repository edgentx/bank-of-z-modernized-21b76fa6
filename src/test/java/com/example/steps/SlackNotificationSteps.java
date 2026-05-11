package com.example.steps;

import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.TemporalPort;
import com.example.mocks.MockSlackAdapter;
import com.example.mocks.MockGitHubAdapter;
import com.example.mocks.MockTemporalAdapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SlackNotificationSteps {
    
    private final MockSlackAdapter mockSlack = new MockSlackAdapter();
    private final MockGitHubAdapter mockGitHub = new MockGitHubAdapter();
    private final MockTemporalAdapter mockTemporal = new MockTemporalAdapter();
    
    private String defectId;
    private String actualSlackMessage;
    private Exception capturedException;
    
    @Given("a defect report is triggered with ID {string}")
    public void aDefectReportIsTriggered(String id) {
        this.defectId = id;
        mockGitHub.setupIssueCreation(id, "Defect: " + id);
    }
    
    @When("the temporal worker executes the report_defect workflow")
    public void theTemporalWorkerExecutesReportDefect() {
        try {
            // This simulates the temporal workflow execution
            mockTemporal.executeReportDefect(defectId);
            
            // After execution, capture what was sent to Slack
            actualSlackMessage = mockSlack.getLastSentMessage();
        } catch (Exception e) {
            capturedException = e;
        }
    }
    
    @Then("the Slack message body should contain a GitHub issue URL")
    public void theSlackMessageBodyShouldContainGitHubIssueURL() {
        assertNotNull("Slack message should not be null", actualSlackMessage);
        
        // Check for the GitHub URL pattern in the message
        assertTrue(
            "Slack message should contain GitHub URL but was: " + actualSlackMessage,
            actualSlackMessage.contains("https://github.com/") && 
            actualSlackMessage.contains("/issues/")
        );
    }
    
    @Then("the GitHub URL should reference defect ID {string}")
    public void theGitHubURLShouldReferenceDefectId(String expectedId) {
        assertTrue(
            "GitHub URL should contain defect ID: " + expectedId,
            actualSlackMessage.contains(expectedId)
        );
    }
    
    @Then("no exception should be thrown during workflow execution")
    public void noExceptionShouldBeThrown() {
        assertNull(
            "Exception should not be thrown: " + 
            (capturedException != null ? capturedException.getMessage() : "null"),
            capturedException
        );
    }
    
    @Then("the Slack notification should include the GitHub link formatted as {string}")
    public void theSlackNotificationShouldIncludeFormattedLink(String expectedFormat) {
        assertTrue(
            "Slack message should contain formatted link: " + expectedFormat,
            actualSlackMessage.contains(expectedFormat)
        );
    }
}