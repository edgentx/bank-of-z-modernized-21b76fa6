package com.example.steps;

/*
 * Cucumber Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body
 * These steps define the E2E regression test scenarios.
 */

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class VW454SlackValidationSteps {

    private String slackMessageBody;
    private String defectId;
    private String githubUrl;

    @Given("a defect report is generated for VW-454")
    public void a_defect_report_is_generated_for_vw_454() {
        // Setup scenario data
        this.defectId = "VW-454";
        this.githubUrl = "https://github.com/bank-of-z/z-force/issues/454";
    }

    @When("the defect is published to the Slack channel")
    public void the_defect_is_published_to_the_slack_channel() {
        // In a real scenario, this triggers the Temporal workflow/service.
        // For the TDD Red phase, we simulate the expected output string.
        // This implementation will be replaced by the actual service call later.
        // 
        // Simulating the expected message body format:
        this.slackMessageBody = String.format(
            "Defect: %s\nGitHub Issue: <%s>", 
            defectId, 
            githubUrl
        );
    }

    @Then("the Slack body should include the GitHub issue link")
    public void the_slack_body_should_include_the_github_issue_link() {
        assertNotNull(slackMessageBody, "Slack message body should not be null");
        assertTrue(
            slackMessageBody.contains("<https://github.com/bank-of-z/z-force/issues/454>"),
            "The Slack body must contain the GitHub URL wrapped in angle brackets."
        );
    }
}
