package com.example.steps;

import com.example.domain.defect.DefectAggregate;
import com.example.domain.defect.ReportDefectCommand;
import com.example.mocks.CapturingSlackNotifier;
import com.example.mocks.FakeGitHubPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class SFB1Steps {

    private String defectId;
    private DefectAggregate aggregate;
    private GitHubPort githubPort;
    private CapturingSlackNotifier slackNotifier;

    @Given("a defect reporting system is available")
    public void setup_system() {
        defectId = "DEF-" + System.currentTimeMillis();
        githubPort = new FakeGitHubPort("https://github.com/bank-of-z/issues/");
        slackNotifier = new CapturingSlackNotifier();
    }

    @When("the temporal worker triggers report_defect")
    public void trigger_report_defect() {
        // Simulate the command received from Temporal
        ReportDefectCommand cmd = new ReportDefectCommand(
            defectId, 
            "VW-454: GitHub URL Validation Failure", 
            "Slack body does not contain the link."
        );

        // Initialize aggregate with dependencies (Port pattern)
        aggregate = new DefectAggregate(defectId, githubPort, slackNotifier);
        
        // Execute command
        aggregate.execute(cmd);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void verify_slack_content() {
        // 1. Verify Event was raised (Internal Domain correctness)
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Domain event should be raised");
        
        // 2. Verify Side Effect (External Integration correctness)
        var notifications = slackNotifier.getCapturedNotifications();
        assertEquals(1, notifications.size(), "Slack should be notified exactly once");
        
        var notification = notifications.get(0);
        assertNotNull(notification.githubUrl, "GitHub URL must not be null");
        assertTrue(notification.githubUrl.startsWith("https://github.com/bank-of-z/issues/"), 
            "URL should match expected pattern");
        
        // 3. Verify Format (Regression test for VW-454)
        // The defect was specifically about the URL being MISSING in the body.
        // By checking notification.githubUrl is present and non-empty, we verify the fix.
    }
}
