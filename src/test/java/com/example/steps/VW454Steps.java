package com.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * 
 * Context: End-to-End verification that when a defect is reported via
 * the temporal-worker execution, the generated Slack body contains the
 * valid GitHub issue URL.
 */
@SpringBootTest
@CucumberContextConfiguration
public class VW454Steps {

    private String defectId;
    String generatedSlackBody;

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered_via_temporal_worker_exec() {
        // Simulating the workflow trigger
        this.defectId = "VW-454";
    }

    @When("the report_defect workflow completes")
    public void the_report_defect_workflow_completes() {
        // RED PHASE STUB:
        // This is where we would call the actual service/worker to generate the Slack payload.
        // For now, we leave it null or invalid to force the test to fail initially 
        // until the implementation is wired in.
        
        // Simulating a failure case where URL is missing (Actual Behavior before fix)
        // this.generatedSlackBody = "Defect reported but no link.";
        
        // To strictly follow TDD RED phase, we expect the implementation to be missing.
        // We will pretend the service returns an empty string or unformatted text.
        this.generatedSlackBody = "Issue reported: " + defectId; 
    }

    @Then("the Slack body should include the GitHub issue URL")
    public void the_slack_body_should_include_the_github_issue_url() {
        // Expected format: <https://github.com/org/repo/issues/454>
        // or similar GitHub URL structure.
        boolean hasLink = false;
        
        if (generatedSlackBody != null) {
            // We look for 'github.com' and the issue ID pattern to satisfy the requirement.
            hasLink = generatedSlackBody.contains("github.com") && generatedSlackBody.contains(defectId);
        }

        // TDD Red Phase Assertion: This will fail because generatedSlackBody currently lacks the URL.
        assertTrue(hasLink, 
            "Expected Slack body to contain GitHub URL for " + defectId + 
            ", but got: " + generatedSlackBody);
    }
}
