package com.example.steps;

import com.example.adapters.SlackMessageValidatorImpl;
import com.example.domain.shared.SlackMessageValidator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps for validating VForce360 Slack integration (VW-454).
 * Regression Test Suite: S-FB-1
 */
public class VForce360ValidationSteps {

    private SlackMessageValidator validator;
    private String actualMessageBody;
    private boolean validationResult;

    public VForce360ValidationSteps() {
        // Using the actual implementation class here to test against the contract.
        // In a full Spring context, this might be injected, but for unit tests, direct instantiation is fine.
        this.validator = new SlackMessageValidatorImpl();
    }

    @Given("a defect report is generated with GitHub URL {string}")
    public void a_defect_report_is_generated_with_github_url(String url) {
        this.actualMessageBody = "Defect reported via VForce360 PM diagnostic conversation. GitHub Issue: " + url;
    }

    @Given("a defect report is generated without a GitHub URL")
    public void a_defect_report_is_generated_without_a_github_url() {
        this.actualMessageBody = "Defect reported via VForce360 PM diagnostic conversation. GitHub Issue: TBD";
    }

    @When("the validation logic runs")
    public void the_validation_logic_runs() {
        this.validationResult = validator.containsGitHubIssueUrl(this.actualMessageBody);
    }

    @Then("the Slack body should contain a valid GitHub issue link")
    public void the_slack_body_should_contain_a_valid_github_issue_link() {
        assertTrue(validationResult, "Expected Slack body to contain a valid GitHub issue URL, but validation failed.");
    }

    @Then("the Slack body should be marked as invalid")
    public void the_slack_body_should_be_marked_as_invalid() {
        assertFalse(validationResult, "Expected Slack body to be invalid (missing URL), but validation passed.");
    }
}
