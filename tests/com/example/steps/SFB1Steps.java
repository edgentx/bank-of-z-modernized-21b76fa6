package com.example.steps;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.adapters.SlackMessageValidatorImpl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class SFB1Steps {

    private String githubUrl;
    private String generatedSlackBody;
    private ReportDefectCmd cmd;
    private final SlackMessageValidatorImpl validator = new SlackMessageValidatorImpl();
    private boolean isValid;

    @Given("a defect is reported with GitHub URL {string}")
    public void a_defect_is_reported_with_github_url(String url) {
        this.githubUrl = url;
        this.cmd = new ReportDefectCmd("S-FB-1", "VW-454 Validation", "URL missing in Slack body", url, java.util.Map.of());
    }

    @When("the Slack body is generated including the defect details")
    public void the_slack_body_is_generated_including_the_defect_details() {
        // Simulating the generation of the Slack body
        this.generatedSlackBody = String.format(
            "Defect Reported: %s\nDescription: %s\nGitHub Issue: <%s>",
            cmd.title(), cmd.description(), cmd.githubUrl()
        );
    }

    @When("I validate the Slack body against the command")
    public void i_validate_the_slack_body_against_the_command() {
        this.isValid = validator.validate(cmd, generatedSlackBody);
    }

    @Then("the validation should pass confirming the URL is present")
    public void the_validation_should_pass_confirming_the_url_is_present() {
        assertTrue(isValid, "Validation should pass if URL is present");
    }
}
