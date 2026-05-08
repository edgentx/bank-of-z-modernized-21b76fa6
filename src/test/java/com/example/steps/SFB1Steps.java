package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 */
public class SFB1Steps {

    private MockSlackNotificationPort slackPort;
    private MockGitHubIssuePort gitHubPort;
    private DefectAggregate aggregate;
    private Exception caughtException;

    @Given("a defect reporting workflow is initialized")
    public void initWorkflow() {
        slackPort = new MockSlackNotificationPort();
        gitHubPort = new MockGitHubIssuePort();
        // We do NOT execute the command yet, just setup the aggregate with mocks
        aggregate = new DefectAggregate("defect-123", gitHubPort, slackPort);
        caughtException = null;
    }

    @Given("GitHub will return issue URL {string}")
    public void setupGitHubUrl(String url) {
        gitHubPort.setSimulatedUrl(url);
    }

    @When("the defect report command is triggered with title {string}")
    public void triggerReport(String title) {
        try {
            ReportDefectCommand cmd = new ReportDefectCommand(
                "defect-123",
                title,
                "Reproduction steps...",
                "LOW"
            );
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void verifySlackBodyContainsLink() {
        // 1. Verify no exception occurred during the process
        assertNull(caughtException, "Workflow should complete without error");

        // 2. Verify exactly one message was sent to Slack
        var messages = slackPort.getMessages();
        assertFalse(messages.isEmpty(), "Slack should have received a message");
        assertEquals(1, messages.size(), "Slack should have received exactly one message");

        // 3. Verify the content of the message contains the expected URL
        var msg = messages.get(0);
        assertTrue(msg.body.contains("https://github.com/example/repo/issues/1"),
            "Slack body must contain the valid GitHub issue URL");
    }

    @Then("the Slack body should not be blank")
    public void verifySlackBodyNotBlank() {
        var messages = slackPort.getMessages();
        assertFalse(messages.isEmpty(), "Slack messages list should not be empty");
        assertNotNull(messages.get(0).body, "Slack body should not be null");
        assertFalse(messages.get(0).body.isBlank(), "Slack body should not be blank");
    }
}
