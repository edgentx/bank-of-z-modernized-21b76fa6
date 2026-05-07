package com.example.steps;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class SFB1Steps {

    private VForce360Aggregate aggregate;
    private Exception capturedException;

    @Given("a defect report exists with ID {string}")
    public void a_defect_report_exists_with_id(String defectId) {
        this.aggregate = new VForce360Aggregate(defectId);
    }

    @When("the defect is reported via Temporal worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec() {
        // This step represents the trigger mechanism
        // Actual Temporal testing happens in the unit test, this drives the domain behavior
        String defectId = "VW-454";
        String githubUrl = "https://github.com/bank-of-z/issues/454";
        String slackBody = "Defect detected: VW-454. See " + githubUrl;
        
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, githubUrl, slackBody);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_the_github_issue_link() {
        Assertions.assertNull(capturedException, "Should not throw exception if URL is present");
        Assertions.assertTrue(aggregate.isReported());
        Assertions.assertEquals("https://github.com/bank-of-z/issues/454", aggregate.getGithubUrl());
    }

    @When("the defect is reported without the GitHub link in the body")
    public void the_defect_is_reported_without_the_github_link_in_the_body() {
        String defectId = "VW-454";
        String githubUrl = "https://github.com/bank-of-z/issues/454";
        String slackBody = "Defect detected: VW-454."; // Missing link

        ReportDefectCmd cmd = new ReportDefectCmd(defectId, githubUrl, slackBody);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("validation fails reporting the missing link")
    public void validation_fails_reporting_the_missing_link() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException.getMessage().contains("Validation Failed"));
        Assertions.assertTrue(capturedException.getMessage().contains("Slack body must contain the GitHub Issue URL"));
    }
}
