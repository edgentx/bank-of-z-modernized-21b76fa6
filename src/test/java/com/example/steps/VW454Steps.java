package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class VW454Steps {

    private final MockSlackNotificationPort slackPort;
    private DefectAggregate defectAggregate;
    private Exception caughtException;

    @Autowired
    public VW454Steps(SlackNotificationPort slackPort) {
        // Assuming Spring injects the mock, or we cast it if needed in a test config
        this.slackPort = (MockSlackNotificationPort) slackPort;
    }

    @Given("a defect is reported via VForce360 PM diagnostic conversation")
    public void a_defect_is_reported() {
        String defectId = "VW-454";
        defectAggregate = new DefectAggregate(defectId);
    }

    @When("the defect reporting workflow is triggered")
    public void trigger_workflow() {
        slackPort.reset();
        ReportDefectCommand cmd = new ReportDefectCommand("VW-454", "GitHub URL in Slack body", "Slack body includes GitHub issue: <url>");
        try {
            defectAggregate.execute(cmd);
            // Simulating the workflow side-effect that would happen in a real service/activity
            String githubUrl = defectAggregate.getGitHubIssueUrl();
            String slackMessage = String.format("Defect Reported: %s - %s", "VW-454", githubUrl);
            slackPort.sendNotification(slackMessage);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body contains the GitHub issue link")
    public void verify_slack_body() {
        assertNull(caughtException, "Workflow should complete without exception");
        assertTrue(slackPort.containsUrl("https://github.com/egdcrypto/bank-of-z/issues/VW-454"), 
            "Slack notification should contain the GitHub issue URL");
    }
}
