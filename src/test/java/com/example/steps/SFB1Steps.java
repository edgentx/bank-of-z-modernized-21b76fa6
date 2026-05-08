package com.example.steps;

import com.example.domain.report_defect.port.*;
import com.example.domain.report_defect.model.ReportDefectCommand;
import com.example.domain.report_defect.model.ReportDefectAggregate;
import com.example.domain.report_defect.model.Severity;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * This acts as the End-to-End (E2E) regression test wrapper.
 */
public class SFB1Steps {

    private MockGithubPort githubPort;
    private MockSlackPort slackPort;
    private ReportDefectAggregate aggregate;
    private Exception captureException;
    private String lastSlackBody;

    // Dependency Injection setup (usually handled by Spring/Cucumber, simplified here)
    public SFB1Steps() {
        this.githubPort = new MockGithubPort();
        this.slackPort = new MockSlackPort();
    }

    @Given("a temporal worker execution is triggered for defect reporting")
    public void a_temporal_worker_execution_is_triggered() {
        // Setup context: Reset mocks
        githubPort.reset();
        slackPort.reset();
    }

    @When("the defect VW-454 is reported with severity LOW")
    public void the_defect_is_reported() {
        try {
            String defectId = "VW-454";
            String description = "Validating VW-454 — GitHub URL in Slack body";
            
            // Configure Mock: GitHub returns a specific URL
            String expectedGithubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
            githubPort.setMockUrl(expectedGithubUrl);

            aggregate = new ReportDefectAggregate(defectId, githubPort, slackPort);
            var cmd = new ReportDefectCommand(defectId, description, Severity.LOW, "validation");
            
            // Execute - simulating the Temporal activity logic
            aggregate.execute(cmd);
            
            // Capture what was sent to Slack
            lastSlackBody = slackPort.getLastPostedBody();

        } catch (Exception e) {
            captureException = e;
        }
    }

    @Then("the Slack message body should contain the GitHub issue URL")
    public void the_slack_message_body_should_contain_the_github_url() {
        if (captureException != null) {
            fail("Step execution failed with exception: " + captureException.getMessage());
        }

        assertNotNull(lastSlackBody, "Slack payload was not generated or sent");
        
        // The core assertion for S-FB-1
        String expectedUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        assertTrue(
            lastSlackBody.contains(expectedUrl), 
            "Slack body should contain GitHub URL: " + expectedUrl + "\nActual Body: " + lastSlackBody
        );
        
        // Additionally ensure it's formatted as requested in the story (<url>)
        assertTrue(
            lastSlackBody.contains("<" + expectedUrl + ">"),
            "Slack body should contain formatted GitHub URL"
        );
    }
}
