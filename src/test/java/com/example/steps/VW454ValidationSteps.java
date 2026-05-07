package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.domain.validation.model.DefectReportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class VW454ValidationSteps {

    @Autowired
    private SlackNotificationPort slackNotificationPort;

    private DefectReportedEvent event;
    private Exception caughtException;

    @Given("a defect is reported via temporal-worker exec")
    public void a_defect_is_reported_via_temporal_worker_exec() {
        // This simulates the Temporal activity triggering the domain logic
        // In a real E2E test, the temporal worker would pick up the message.
        // Here we instantiate the event that would be published.
        this.event = new DefectReportedEvent(
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            "Fix: Validating VW-454",
            "Checking Slack body for GitHub issue link",
            "LOW",
            "validation",
            Instant.now()
        );
    }

    @When("the Slack notification is processed")
    public void the_slack_notification_is_processed() {
        try {
            // Assuming the port/handler processes this event to generate a Slack payload
            // The MockSlackNotificationPort captures the "sent" body.
            slackNotificationPort.sendAlert(
                String.format("[%s] %s", event.severity(), event.title()),
                String.format("Description: %s\nProject: %s", event.description(), event.aggregateId())
            );
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the Slack body includes GitHub issue url")
    public void the_slack_body_includes_github_issue_url() {
        // Red Phase: Verification Logic
        // The implementation of MockSlackNotificationPort needs to expose what it received.
        // However, we are writing the Port interface here too.
        // We assume the Port implementation is a Mock we can inspect.
        // If the Mock does not store the body, this test fails (Red).
        
        // In a real setup, we might inject a Mockito mock or a Test Spy.
        // Here we simulate the assertion requirement:
        
        // String sentBody = slackNotificationPort.getLastSentBody(); // This method likely doesn't exist yet -> FAIL
        
        // For the sake of the generated code, we will assume the implementation is missing.
        fail("Red Phase: The mechanism to verify the Slack body content (GitHub URL) is not implemented or the body is missing the URL.");
    }
}
