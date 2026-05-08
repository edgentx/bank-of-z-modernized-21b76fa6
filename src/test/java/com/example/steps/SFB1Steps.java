package com.example.steps;

import com.example.domain.shared.DefectReportedEvent;
import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.validation.model.DefectAggregate;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Scenario;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-FB-1.
 * Verifies that the Slack body generated contains the GitHub URL.
 */
public class SFB1Steps {

    // In a real Spring Boot integration, we would @Autowired the handler.
    // For the TDD Red Phase, we instantiate the Aggregate and Mocks directly
    // to ensure the test fails if the logic is missing.
    
    private final MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
    private DefectAggregate aggregate;
    private String capturedGitHubUrl;

    @Given("a defect report with GitHub URL {string}")
    public void a_defect_report_with_github_url(String url) {
        // We assume the defect ID is generated or fixed for the test scenario
        this.aggregate = new DefectAggregate("DEF-454");
        this.capturedGitHubUrl = url;
    }

    @When("the temporal-worker executes the _report_defect workflow")
    public void the_temporal_worker_executes_the_report_defect_workflow() {
        // Simulate the workflow processing the command
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-454",
            "Validating VW-454",
            "GitHub URL in Slack body",
            "VForce360 PM",
            capturedGitHubUrl
        );

        // Execute domain logic
        aggregate.execute(cmd);

        // NOTE: In a real implementation, a Projector/Handler would listen to DefectReportedEvent
        // and call SlackNotificationPort.sendNotification().
        // Since this is a Unit/Integration Test in Red Phase, we manually trigger the logic under test
        // or verify the state.
        
        // For this specific story (S-FB-1), we are validating the *content* of the Slack body.
        // If the logic to put the URL in the body exists, it should be testable.
        // Here we simulate the Projection logic that would send the message:
        String slackBody = generateSlackBodyFromEvent(cmd); // This is the method under test conceptually
        mockSlack.sendNotification(slackBody);
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        // Verify the mock received the message containing the URL
        assertTrue(mockSlack.wasCalledWith(capturedGitHubUrl), 
            "Slack body should contain the GitHub URL: " + capturedGitHubUrl);
        
        // Specifically verify the format <url>
        assertTrue(mockSlack.wasCalledWith("<" + capturedGitHubUrl + ">"),
            "Slack body should format URL as <" + capturedGitHubUrl + ">");
    }

    /**
     * Placeholder for the Projection logic.
     * This method represents the code that needs to be written/fixed.
     * Currently returns empty string to force a test failure (Red Phase).
     */
    private String generateSlackBodyFromEvent(ReportDefectCmd event) {
        // INTENTIONAL BUG FOR RED PHASE: Missing URL in body
        return "Defect Reported: " + event.title();
    }
}
