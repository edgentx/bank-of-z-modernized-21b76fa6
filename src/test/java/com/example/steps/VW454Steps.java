package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test steps for validating VW-454: GitHub URL in Slack body.
 * This class represents the RED phase of TDD. The implementation does not exist yet,
 * so these tests will fail until the logic is added.
 */
public class VW454Steps {

    // We use the specific mock type to access the verification helper methods
    private MockSlackNotificationPort mockSlack;

    // In a real Spring Context test, this would be @Autowired. 
    // For this TDD artifact, we assume the context wires the Mock.
    public VW454Steps(SlackNotificationPort slackPort) {
        if (!(slackPort instanceof MockSlackNotificationPort)) {
            throw new IllegalStateException("Tests must use MockSlackNotificationPort");
        }
        this.mockSlack = (MockSlackNotificationPort) slackPort;
    }

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered_via_temporal_worker_exec() {
        // Setup: Ensure the mock is clean before the scenario
        mockSlack.clear();
        
        // Context: In the real application, Temporal triggers a workflow.
        // We simulate the invocation of that business logic here directly or via a facade.
        // For the RED phase, we are defining the behavior expectations.
    }

    @When("the report defect workflow completes successfully")
    public void the_report_defect_workflow_completes_successfully() {
        // Simulate the Workflow Logic
        // This is the placeholder for the system-under-test (SUT) invocation.
        // We do not have the SUT yet, so we simulate what we expect it to do.
        // 
        // NOTE: In a real integration test, we would call: workflowService.reportDefect(...);
        // Since we are writing the test FIRST, we manually invoke the mock's receive
        // path with the EXPECTED data to validate the assertion logic, or we leave this empty
        // to prove the SUT is missing. Let's assume we call a placeholder service.
        
        // Code to be written later:
        // defectService.report("VW-454", "https://github.com/...");
        
        // For the sake of the test structure, we will manually trigger the port 
        // with a "Bad" message (missing URL) first to prove the test fails (TDD Red),
        // OR we leave the implementation empty so the assertion fails.
        // Let's assume the implementation sends nothing yet.
    }

    @Then("the Slack body includes the GitHub issue link")
    public void the_slack_body_includes_the_github_issue_link() {
        // This assertion checks the Acceptance Criteria: "Slack body includes GitHub issue: <url>"
        // This will FAIL because the workflow (When step) hasn't sent anything yet.
        
        // 1. Check a message was sent
        assertTrue(!mockSlack.getSentMessages().isEmpty(), "No Slack messages were sent by the workflow");
        
        // 2. Check content
        String lastBody = mockSlack.getSentMessages().get(mockSlack.getSentMessages().size() - 1);
        
        // This is the specific check for the defect VW-454
        // We expect the URL to be present.
        assertNotNull(lastBody, "Message body should not be null");
        assertTrue(
            lastBody.contains("github.com") || lastBody.contains("http"), 
            "Slack body should contain a GitHub URL, but was: " + lastBody
        );
    }
}