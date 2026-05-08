package com.example.steps;

import com.example.adapters.GitHubIssueTrackerAdapter;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationReportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454.
 * Ensures that when a defect is reported, the resulting event (or Slack body representation)
 * contains the actual GitHub URL.
 */
public class Vw454Steps {

    @Autowired(required = false)
    private GitHubIssueTrackerAdapter issueTracker;

    private String actualUrl;
    private Exception executionException;

    @Given("the GitHub adapter is available")
    public void the_github_adapter_is_available() {
        // Context setup. In a real test, we might mock the HTTP client, but here
        // we are testing the logic of the adapter/pipeline assembly.
        assertNotNull(issueTracker, "GitHubIssueTrackerAdapter should be wired");
    }

    @When("_report_defect is triggered via temporal-worker exec")
    public void report_defect_is_triggered() {
        // Simulate the command execution that would happen in the Temporal workflow
        // Project ID matches the story description
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        
        try {
            // This is the interaction that would generate the event containing the URL
            actualUrl = issueTracker.reportDefect(
                projectId, 
                "VW-454 Regression Test", 
                "Verifying GitHub URL presence in Slack body."
            );
        } catch (Exception e) {
            executionException = e;
        }
    }

    @Then("Slack body includes GitHub issue: {string}")
    public void slack_body_includes_github_issue(String expectedUrlPrefix) {
        if (executionException != null) {
            fail("Execution failed with exception: " + executionException.getMessage());
        }

        assertNotNull(actualUrl, "The generated URL should not be null");
        
        // Expected Behavior: Slack body includes GitHub issue: <url>
        // Here we validate that the URL string is present and well-formed according to the defect requirements
        assertTrue(
            actualUrl.startsWith("https://github.com/"),
            "URL should start with https://github.com/ but was: " + actualUrl
        );
        
        // Ensure it's not just the prefix, but includes the issue ID/number part
        assertTrue(
            actualUrl.length() > "https://github.com/".length(),
            "URL seems incomplete or missing issue ID"
        );
    }

    @Then("the validation no longer exhibits the reported behavior")
    public void validation_no_longer_exhibits_reported_behavior() {
        // Regression check: Ensure we don't return an empty string or "About to find out"
        assertNotNull(actualUrl, "URL should not be null (regression check)");
        assertFalse(
            actualUrl.contains("About to find out"),
            "URL should not contain placeholder text 'About to find out'"
        );
        assertFalse(
            actualUrl.isEmpty(),
            "URL should not be empty"
        );
    }
}
