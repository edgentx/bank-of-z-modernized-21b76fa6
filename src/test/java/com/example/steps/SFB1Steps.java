package com.example.steps;

import com.example.ports.VForce360Port;
import com.example.mocks.MockVForce360Adapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating VForce360 Slack Body content.
 * TDD Red Phase: Validates that defect reporting injects the GitHub URL into the Slack payload.
 */
public class SFB1Steps {

    @Autowired
    private VForce360Port vForce360Port; // Will be injected as MockVForce360Adapter

    private String capturedUrl;
    private String capturedTitle;
    private Exception executionException;

    @Given("a GitHub issue URL {string}")
    public void aGitHubIssueURL(String url) {
        this.capturedUrl = url;
    }

    @Given("a defect title {string}")
    public void aDefectTitle(String title) {
        this.capturedTitle = title;
    }

    @When("the defect is reported via Temporal workflow")
    public void theDefectIsReportedViaTemporalWorkflow() {
        try {
            // This simulates the Temporal worker invoking the port
            vForce360Port.reportDefect(capturedTitle, capturedUrl);
        } catch (Exception e) {
            this.executionException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void theSlackBodyShouldContainTheGitHubIssueLink() {
        assertNull(executionException, "Workflow execution should not throw exception");

        if (vForce360Port instanceof MockVForce360Adapter mock) {
            var messages = mock.getSentMessages();
            assertFalse(messages.isEmpty(), "Slack message should have been sent");

            var sent = messages.get(0);
            assertEquals(capturedTitle, sent.title, "Title should match");
            
            // Critical validation for VW-454: Ensure the URL is present in the body
            assertTrue(sent.body.contains(capturedUrl), 
                "Slack body must contain GitHub URL [" + capturedUrl + "]. Actual body: " + sent.body);
        } else {
            throw new IllegalStateException("Test configuration error: Expected MockVForce360Adapter");
        }
    }
}
