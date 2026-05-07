package com.example.steps;

import com.example.domain.vforce.model.DefectReportingAggregate;
import com.example.domain.vforce.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for validating Story VW-454.
 * Scenario: Trigger report_defect -> Verify Slack body contains URL.
 */
public class VW454Steps {

    // These would be injected by the Test Context in a real Spring/ cucumber setup
    private MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
    private DefectReportingAggregate aggregate;
    private Exception capturedException;

    @Given("a defect report is triggered for VW-454")
    public void a_defect_report_is_triggered() {
        aggregate = new DefectReportingAggregate("VW-454");
    }

    @When("the temporal worker executes the report_defect command")
    public void the_worker_executes_the_command() {
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454", 
            "GitHub URL in Slack body", 
            "Verify the link is present", 
            "LOW", 
            "DEFECT"
        );
        
        try {
            var events = aggregate.execute(cmd);
            // Simulate the Handler/Application layer processing events
            if (!events.isEmpty()) {
                slackPort.sendDefectAlert("#vforce360-issues", events.get(0));
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body includes the GitHub issue link")
    public void the_slack_body_includes_link() {
        assertNull(capturedException, "Command execution should not throw exception");
        
        var messages = slackPort.getSentMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a message");
        
        var msg = messages.get(0);
        assertNotNull(msg.githubUrl, "GitHub URL must be present in the processed message");
        assertTrue(msg.body.contains(msg.githubUrl), "Message body must contain the URL");
        assertTrue(msg.body.contains("github.com"), "Body must reference github.com");
    }
}
