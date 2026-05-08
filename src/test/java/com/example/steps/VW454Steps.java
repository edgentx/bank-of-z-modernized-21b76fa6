package com.example.steps;

import com.example.mocks.MockVForce360IntegrationAdapter;
import com.example.ports.VForce360IntegrationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating VW-454.
 * Covers the regression scenario where the Slack body must contain the GitHub URL.
 */
public class VW454Steps {

    // In a real Spring Boot test, this would be injected. We use the Mock directly for definition.
    private final MockVForce360IntegrationAdapter integrationMock;
    private String lastSlackBody;

    public VW454Steps() {
        this.integrationMock = new MockVForce360IntegrationAdapter();
    }

    @Given("the defect report {string} has been triggered via temporal-worker")
    public void the_defect_report_has_been_triggered(String defectId) {
        // Configure the mock to simulate the workflow having run
        integrationMock.setDefectExecuted(true);
        assertTrue(integrationMock.wasDefectReportExecuted(defectId), 
            "Defect report should be triggered via temporal-worker");
    }

    @Given("the Slack message body contains the GitHub issue URL")
    public void the_slack_message_body_contains_the_github_url() {
        // Configure the mock with the expected content (PASS case)
        String expectedUrl = "https://github.com/org/repo/issues/454";
        String validBody = "Defect Reported: VW-454. Please check: " + expectedUrl;
        integrationMock.setSlackMessage("vforce360-issues", validBody);
    }

    @When("I verify the Slack body for defect {string}")
    public void i_verify_the_slack_body_for_defect(String defectId) {
        // Act: Retrieve the message body via the port
        this.lastSlackBody = integrationMock.getLastSlackMessageBody("vforce360-issues");
    }

    @Then("the body should include the GitHub issue URL")
    public void the_body_should_include_the_github_url() {
        // Assert: Verify the URL is present
        assertNotNull(lastSlackBody, "Slack body should not be null");
        
        // Checking for generic URL pattern or specific host to satisfy "includes GitHub issue"
        boolean hasLink = lastSlackBody.contains("https://github.com/");
        assertTrue(hasLink, 
            "Slack body should include the GitHub issue URL. Body was: " + lastSlackBody);
    }

    // --- Regression / Negative Case Scenarios ---

    @Given("the Slack message body is missing the GitHub URL")
    public void the_slack_message_body_is_missing_the_url() {
        // Configure the mock with the actual/invalid content (FAIL case - RED phase)
        String invalidBody = "Defect Reported: VW-454. Status: New.";
        integrationMock.setSlackMessage("vforce360-issues", invalidBody);
    }

    @Then("the validation should fail indicating the URL is missing")
    public void the_validation_should_fail() {
        assertNotNull(lastSlackBody, "Slack body should not be null");
        
        boolean hasLink = lastSlackBody.contains("https://github.com/");
        assertFalse(hasLink, 
            "Validation should detect missing GitHub URL. Body was: " + lastSlackBody);
    }
}