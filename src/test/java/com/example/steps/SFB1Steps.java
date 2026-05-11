package com.example.steps;

import com.example.adapters.DefectReportTemporalAdapter;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

/**
 * Cucumber Steps for S-FB-1.
 * TDD Red Phase: These tests define the expected behavior.
 */
public class SFB1Steps {

    private MockSlackPort mockSlack = new MockSlackPort();
    private MockGitHubPort mockGitHub = new MockGitHubPort();
    private DefectReportTemporalAdapter adapter;
    private String resultUrl;

    @Given("the defect reporting system is initialized")
    public void setup() {
        adapter = new DefectReportTemporalAdapter(mockSlack, mockGitHub);
        mockGitHub.setMockUrl("https://github.com/egdcrypto/bank-of-z/issues/454");
    }

    @When("the temporal worker executes the defect report for VW-454")
    public void trigger_report_defect() {
        // Simulate Temporal Worker invoking the activity
        resultUrl = adapter.executeReportDefect("conv-123", "VW-454");
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void verify_slack_body() {
        // Primary assertion for S-FB-1
        String body = mockSlack.lastBody;
        
        Assertions.assertNotNull(body, "Slack body should not be null");
        Assertions.assertTrue(
            body.contains("GitHub issue: https://github.com/egdcrypto/bank-of-z/issues/454"),
            "Slack body must include 'GitHub issue: <url>'. Actual body: " + body
        );
    }
    
    @Then("the validation no longer exhibits the reported behavior")
    public void verify_fix() {
        // Regression check: ensure the link is present and formatted correctly
        String body = mockSlack.lastBody;
        Assertions.assertFalse(body.isEmpty());
        Assertions.assertTrue(body.contains("http"));
    }
}
