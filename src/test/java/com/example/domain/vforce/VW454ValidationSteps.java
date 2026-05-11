package com.example.domain.vforce;

import com.example.adapters.SlackNotificationPort;
import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.domain.vforce.model.VW454ValidatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * This test suite validates the end-to-end defect reporting workflow.
 */
public class VW454ValidationSteps {

    private final MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
    private final VForce360Aggregate aggregate;
    private Exception capturedException;

    // Assuming the application can inject the mock or we create the aggregate manually.
    // Since we are in TDD Red phase without the implementation class yet, we assume the structure.
    public VW454ValidationSteps() {
        this.aggregate = new VForce360Aggregate("vforce-360-ctx", mockSlack);
    }

    @Given("a defect report for VW-454 is prepared")
    public void a_defect_report_for_vw_454_is_prepared() {
        // No-op setup, just ensuring context
    }

    @When("the temporal worker executes _report_defect command")
    public void the_temporal_worker_executes_report_defect_command() {
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "VALIDATION",
            "Validating GitHub URL in Slack body",
            Map.of("issue_id", "VW-454")
        );
        
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack message body must contain the GitHub issue link")
    public void the_slack_message_body_must_contain_the_github_issue_link() {
        if (capturedException != null) {
            throw new RuntimeException("Execution failed before validation could occur", capturedException);
        }

        List<String> messages = mockSlack.getCapturedBodies();
        Assert.assertFalse("No Slack messages were sent", messages.isEmpty());

        String lastBody = messages.get(messages.size() - 1);
        
        // The requirement states: <url>. We look for a URL pattern containing the issue ID.
        boolean hasLink = lastBody.contains("http") && lastBody.contains("VW-454");
        
        Assert.assertTrue(
            "Slack body should contain the GitHub issue link for VW-454. Body was: " + lastBody,
            hasLink
        );
    }
}
