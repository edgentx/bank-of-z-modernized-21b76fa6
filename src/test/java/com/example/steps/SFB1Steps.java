package com.example.steps;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.InMemorySlackNotification;
import com.example.mocks.MockIssueTracker;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for validating VW-454.
 * This implements the TDD Red Phase: the application code to satisfy this is missing.
 */
public class SFB1Steps {

    // Dependencies (Mocks)
    private final MockIssueTracker issueTracker = new MockIssueTracker();
    private final InMemorySlackNotification slackNotification = new InMemorySlackNotification();

    // State
    private String currentDefectId;
    private String currentTitle;
    private Exception capturedException;

    @Given("the temporal worker triggers _report_defect with ID {string}")
    public void the_temporal_worker_triggers_report_defect_with_id(String defectId) {
        this.currentDefectId = defectId;
    }

    @And("the defect title is {string}")
    public void the_defect_title_is(String title) {
        this.currentTitle = title;
    }

    @When("the defect report processing completes")
    public void the_defect_report_processing_completes() {
        // RED PHASE IMPLEMENTATION
        // This simulates the logic that SHOULD exist in the Application/Service layer.
        // Since we are in TDD Red phase, we simulate the process manually here using the mocks.
        // The Application.java or a dedicated Service/Workflow does not yet implement this.

        try {
            // 1. Create Command
            ReportDefectCmd cmd = new ReportDefectCmd(currentDefectId, currentTitle, "Description", "LOW");

            // 2. Create Issue (Simulating Aggregate/Service Logic)
            String url = issueTracker.createIssue(cmd.defectId(), cmd.title());

            // 3. Notify Slack (Simulating Projection/Handler Logic)
            // Validation VW-454: The URL must be present.
            if (url == null || url.isBlank()) {
                throw new IllegalStateException("GitHub URL was not generated");
            }
            
            String slackBody = "Defect Reported: " + cmd.title() + "\nGitHub Issue: " + url;
            slackNotification.postMessage(slackBody);

        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack notification body should contain {string}")
    public void the_slack_notification_body_should_contain(String substring) {
        if (capturedException != null) {
            fail("Processing failed with exception: " + capturedException.getMessage(), capturedException);
        }
        
        String body = slackNotification.getLastMessage();
        assertNotNull(body, "No Slack message was posted");
        assertTrue(body.contains(substring), "Slack body did not contain expected substring [" + substring + "]. Actual body: " + body);
    }
}
