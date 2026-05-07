package com.example.steps;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.service.VForce360Service;
import com.example.ports.SlackNotifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * TDD Red Phase Implementation.
 */
public class SFb1Steps {

    @Autowired
    private VForce360Service vForce360Service;

    @Autowired
    private SlackNotifier mockSlackNotifier; // Injected via Spring Config as Mockito mock

    private String testGithubUrl;
    private Exception capturedException;

    @Given("a defect report with GitHub issue URL {string}")
    public void a_defect_report_with_github_issue_url(String url) {
        this.testGithubUrl = url;
    }

    @When("the defect is reported via Temporal worker execution")
    public void the_defect_is_reported_via_temporal_worker_execution() {
        try {
            // Simulate the Temporal Activity/Workflow trigger
            // The service should process the command and notify Slack
            vForce360Service.reportDefect(testGithubUrl);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack notification body should include the GitHub issue link")
    public void the_slack_notification_body_should_include_the_github_issue_link() {
        // Verify the interaction
        verify(mockSlackNotifier, times(1)).send(argThat(body -> body != null && body.contains(testGithubUrl)));
    }

    @Then("the validation should pass successfully")
    public void the_validation_should_pass_successfully() {
        assertNull(capturedException, "Expected no exception during defect reporting");
    }
}