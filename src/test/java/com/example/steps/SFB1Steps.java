package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkflowPort;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.mocks.MockTemporalWorkflowAdapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: GitHub URL in Slack body validation.
 * Verifies that when a defect is reported via Temporal, the resulting Slack notification
 * contains the correct GitHub issue URL.
 */
@SpringBootTest
@CucumberContextConfiguration
public class SFB1Steps {

    @Autowired
    private TemporalWorkflowPort temporalPort;

    @Autowired
    private SlackNotificationPort slackPort;

    private String reportedIssueId;

    @Given("the VForce360 PM diagnostic system is operational")
    public void the_system_is_operational() {
        // Preconditions verified via context load
        assertNotNull(temporalPort);
        assertNotNull(slackPort);
    }

    @When("_report_defect is triggered via temporal-worker exec for issue {string}")
    public void report_defect_is_triggered(String issueId) {
        this.reportedIssueId = issueId;
        
        // We are testing the adapter flow. The temporal mock receives the signal,
        // processes it, and delegates to the slack mock.
        temporalPort.executeReportDefectWorkflow(issueId);
    }

    @Then("the Slack body includes GitHub issue {string}")
    public void the_slack_body_includes_github_issue(String expectedUrl) {
        // Retrieve the captured state from the mock
        String actualBody = slackPort.getLastMessageBody();
        
        assertNotNull(actualBody, "Slack message body was null, message not sent");
        assertTrue(actualBody.contains(expectedUrl), 
            "Slack body [" + actualBody + "] did not contain expected URL [" + expectedUrl + "]");
    }
}
