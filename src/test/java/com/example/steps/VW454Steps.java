package com.example.steps;

import com.example.domain.shared.SlackMessageValidator;
import com.example.domain.shared.ValidationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for VW-454: Validating GitHub URL in Slack body.
 * Testing the defect where GitHub links were missing/invalid in VForce360 issue reports.
 */
@SpringBootTest
public class VW454Steps {

    @Autowired
    private ValidationPort validationPort;

    private String slackBodyContent;
    private Exception caughtException;

    @Given("a defect report body containing a valid GitHub URL")
    public void a_defect_report_body_containing_a_valid_github_url() {
        this.slackBodyContent = "Issue tracked at: https://github.com/bank-of-z/core/issues/456";
    }

    @Given("a defect report body missing the GitHub URL")
    public void a_defect_report_body_missing_the_github_url() {
        this.slackBodyContent = "Issue tracked internally. Reference: DB-456.";
    }

    @Given("a defect report body containing a malformed URL")
    public void a_defect_report_body_containing_a_malformed_url() {
        this.slackBodyContent = "Link: github.com/bank-of-z/core/issues/456 (Protocol missing)";
    }

    @When("the temporal worker executes the report_defect workflow validation")
    public void the_temporal_worker_executes_the_report_defect_workflow_validation() {
        try {
            validationPort.validateSlackBody(slackBodyContent);
        } catch (SlackMessageValidator.SlackValidationException e) {
            this.caughtException = e;
        }
    }

    @Then("the validation should pass")
    public void the_validation_should_pass() {
        assertNull(caughtException, "Validation should pass for valid content, but got: " + caughtException);
    }

    @Then("the validation should fail indicating the missing URL")
    public void the_validation_should_fail_indicating_the_missing_url() {
        assertNotNull(caughtException, "Validation should fail for missing URL");
        assertTrue(caughtException.getMessage().contains("GitHub"), "Error message should mention GitHub");
    }

    @Then("the validation should fail indicating the malformed URL")
    public void the_validation_should_fail_indicating_the_malformed_url() {
        assertNotNull(caughtException, "Validation should fail for malformed URL");
        assertTrue(caughtException.getMessage().contains("URL"), "Error message should mention URL format");
    }
}
