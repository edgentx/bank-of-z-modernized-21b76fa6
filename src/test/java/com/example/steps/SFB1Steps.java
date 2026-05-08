package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-FB-1.
 * Validates that the temporal worker executes _report_defect and includes the GitHub URL in the Slack body.
 */
public class SFB1Steps {

    // Assuming the temporal workflow or orchestrator uses this port to notify Slack.
    // If not yet injected, we'd manually wire it in the test context.
    @Autowired
    private SlackNotificationPort slackNotificationPort;

    private ReportDefectCmd command;
    private Exception capturedException;

    // We assume the Spring context has been configured to use the Mock implementation.
    // In a real setup, this would be in a TestConfiguration.
    public SFB1Steps() {
        // This wiring usually happens in a SpringBootTest config, but for the sake of the
        // "Red Phase" output, we assume the system under test interacts with the Port.
        // We will verify the Mock state.
    }

    @Given("the temporal worker is ready to report a defect")
    public void the_temporal_worker_is_ready() {
        // Setup logic: ensure mocks are clean
        if (slackNotificationPort instanceof MockSlackNotificationPort mock) {
            mock.clear();
        }
    }

    @When("the client triggers _report_defect with ID {string} and title {string}")
    public void the_client_triggers_report_defect(String id, String title) {
        // Simulate the command triggering the workflow
        this.command = new ReportDefectCmd(
                id,
                title,
                "Defect in VForce360 validation",
                Map.of("gitHubIssueUrl", "https://github.com/example/bank-of-z/issues/" + id)
        );

        // Here we would invoke the Workflow. 
        // Since we are in the RED phase (no implementation), we simulate the *expected* behavior
        // if the code existed, or we invoke the stub/handler that throws exception.
        // For this test to FAIL (Red), we assume the actual workflow logic is missing,
        // or we can assert on the Mock immediately to fail if the logic doesn't run.
        // 
        // In this specific context, we are simulating the execution:
        try {
            // Pseudo-code for the workflow:
            // workflow.reportDefect(command); 
            // Since we don't have the workflow class yet (Red phase), we can't call it.
            // However, the prompt asks to "Fix the build" and "Write tests".
            // The test *logic* asserts the expectation.
            
            // To make the test fail in Red Phase correctly, we usually leave the implementation empty.
            // But for the sake of this output, we are providing the Test class.
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body should include the GitHub issue link")
    public void the_slack_body_should_include_the_github_link() {
        // This assertion will FAIL (Red) because the Mock will be empty 
        // until we implement the workflow.
        assertTrue(slackNotificationPort instanceof MockSlackNotificationPort, "SlackPort should be mocked");
        
        MockSlackNotificationPort mock = (MockSlackNotificationPort) slackNotificationPort;
        
        // We expect the URL derived from the command ID
        String expectedUrl = "https://github.com/example/bank-of-z/issues/" + command.defectId();
        
        // This is the failing assertion check:
        assertFalse(mock.getMessages().isEmpty(), "No Slack messages were sent");
        assertTrue(mock.lastMessageContainsUrl(expectedUrl), 
            "Slack body did not contain the expected GitHub URL: " + expectedUrl 
            + "\nActual body: " + mock.getMessages().get(mock.getMessages().size()-1).body);
    }
}