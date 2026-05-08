package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454.
 * This bridges the BDD scenario with the Java domain logic.
 */
public class SFB1Steps {

    // In a real Spring Boot test, these might be injected.
    // Here we instantiate them for the scope of the test run.
    private MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
    private MockGitHubIssuePort gitHubPort = new MockGitHubIssuePort();
    
    // Assuming DefectAggregate exists (or we are forcing it to exist via TDD)
    private Object defectAggregate; 
    private Exception capturedException;

    @Given("the temporal worker triggers {string} execution")
    public void the_temporal_worker_triggers_execution(String string) {
        // Setup phase: Simulate the Temporal activity starting
        // In code, this initializes the Aggregate/Handler
        // defectAggregate = new DefectAggregate(..., gitHubPort, slackPort);
        slackPort.clear();
    }

    @When("the defect VW-454 is reported with severity LOW")
    public void the_defect_vw_454_is_reported_with_severity_low() {
        // Execute the report defect command
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454",
            "Checking Slack body for link",
            "LOW",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        
        try {
            // defectAggregate.execute(cmd);
            // Since we don't have the class implementation yet, we mock the behavior 
            // or expect this step to fail until implementation is added.
            // For strict TDD, we leave the call that will fail to compile.
            throw new UnsupportedOperationException("Pending implementation of DefectAggregate");
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body contains GitHub issue {string}")
    public void the_slack_body_contains_github_issue(String url) {
        // Verify the mock received the correct payload
        if (capturedException != null) {
            fail("Defect reporting failed: " + capturedException.getMessage());
        }

        assertFalse(slackPort.getSentMessages().isEmpty());
        String body = slackPort.getSentMessages().get(0).body();
        assertTrue(body.contains(url), "Expected Slack body to contain " + url + " but found: " + body);
    }
}
