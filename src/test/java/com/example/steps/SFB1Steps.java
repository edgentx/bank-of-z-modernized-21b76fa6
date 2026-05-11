package com.example.steps;

import com.example.domain.defect.ReportDefectCommand;
import com.example.domain.defect.Service;
import com.example.mocks.MockDefectRepository;
import com.example.mocks.MockSlackPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Behavior:
 * 1. Trigger _report_defect via temporal-worker exec (simulated by Service call)
 * 2. Verify Slack body contains GitHub issue link
 */
public class SFB1Steps {

    // Mocks
    private final MockDefectRepository repository = new MockDefectRepository();
    private final MockSlackPort slackPort = new MockSlackPort();

    // System Under Test
    private Service defectService;

    // Inputs
    private ReportDefectCommand cmd;

    // Exception Capture
    private Exception capturedException;

    @Given("the defect reporting system is initialized")
    public void the_defect_reporting_system_is_initialized() {
        defectService = new Service(repository, slackPort);
        slackPort.clear();
    }

    @Given("a valid defect report command with URL {string}")
    public void a_valid_defect_report_command_with_url(String url) {
        // Construct a valid command
        this.cmd = new ReportDefectCommand("defect-123", "Test Defect", url);
    }

    @When("the report defect command is executed")
    public void the_report_defect_command_is_executed() {
        try {
            defectService.reportDefect(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("Slack should receive a notification containing the GitHub issue link")
    public void slack_should_receive_a_notification_containing_the_github_issue_link() {
        var messages = slackPort.getMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a message");
        
        var postedMsg = messages.get(0);
        assertEquals("#vforce360-issues", postedMsg.channel, "Message should go to the correct channel");
        
        // This is the core assertion for VW-454
        assertTrue(postedMsg.text.contains(cmd.githubUrl()), 
            "Slack body must contain the full GitHub URL provided in the command");
    }

    // Negative Test Case: Ensure we don't post garbage
    @Then("Slack should not receive any notification")
    public void slack_should_not_receive_any_notification() {
        assertTrue(slackPort.getMessages().isEmpty(), "Slack should not receive messages for failed commands");
    }
}
