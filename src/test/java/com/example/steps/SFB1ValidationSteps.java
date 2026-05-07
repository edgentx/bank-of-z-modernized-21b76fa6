package com.example.steps;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.DefectReporterPort;
import com.example.mocks.MockSlackNotificationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

/**
 * Cucumber Steps for validating S-FB-1 (VW-454).
 * Tests that the Slack body contains the GitHub URL.
 */
@SpringBootTest
public class SFB1ValidationSteps {

    @Autowired
    private DefectReporterPort reporter;

    private ReportDefectCmd cmd;
    private String actualBody;
    private Exception caughtException;

    @Given("a defect report command exists for VW-454")
    public void a_defect_report_command_exists_for_vw_454() {
        // Setup data matching the reproduction steps
        this.cmd = new ReportDefectCmd(
                "VW-454",
                "Validating VW-454 — GitHub URL in Slack body",
                "Slack body includes GitHub issue link",
                "LOW",
                "validation",
                "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        // Ensure no prior state leakage
        this.actualBody = null;
        this.caughtException = null;
    }

    @When("the defect is reported via temporal-worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec() {
        try {
            // Trigger the report generation (synchronous inspection)
            this.actualBody = reporter.generateBodyPreview(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue URL")
    public void the_slack_body_should_contain_the_github_issue_url() {
        if (caughtException != null) {
            fail("Exception occurred during reporting: " + caughtException.getMessage());
        }
        assertNotNull("Actual body should not be null", actualBody);
        
        // The core validation for S-FB-1
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/VW-454";
        assertTrue("Slack body should contain the full GitHub URL: " + actualBody, 
                   actualBody.contains(expectedUrl));
        
        // Also verify Slack link format <url|text>
        assertTrue("Slack body should contain the formatted Slack link", 
                   actualBody.contains("<" + expectedUrl + "|"));
    }

    @Then("the validation should pass without errors")
    public void the_validation_should_pass_without_errors() {
        assertNull("No exception should have been thrown", caughtException);
    }
}
