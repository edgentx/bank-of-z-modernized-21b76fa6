package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.SlackNotificationPostedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Cucumber Steps for S-FB-1: Regression test validating VW-454.
 * End-to-end verification via Temporal -> Domain -> Slack body content.
 */
public class SFB1Steps {

    private String defectId;
    private String githubUrl;
    private ReportDefectCmd cmd;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a defect report {string} linked to GitHub issue {string}")
    public void a_defect_report_linked_to_github_issue(String id, String url) {
        this.defectId = id;
        this.githubUrl = url;
    }

    @When("the defect is reported via Temporal worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec() {
        // This step simulates the Temporal activity invoking the domain logic
        cmd = new ReportDefectCmd(
            defectId,
            "Reproducing VW-454: Validating GitHub URL in Slack body",
            "LOW",
            githubUrl
        );

        try {
            // In a real integration test, we might invoke the Temporal workflow.
            // For domain verification, we instantiate the aggregate directly.
            com.example.domain.validation.ValidationAggregate aggregate = 
                new com.example.domain.validation.ValidationAggregate(defectId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the Slack body should include the GitHub issue link {string}")
    public void the_slack_body_should_include_the_github_issue_link(String expectedUrl) {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should produce one event");

        Assertions.assertTrue(resultEvents.get(0) instanceof SlackNotificationPostedEvent, 
            "Event should be SlackNotificationPostedEvent");

        SlackNotificationPostedEvent event = (SlackNotificationPostedEvent) resultEvents.get(0);
        
        // CRITICAL AC: The validation no longer exhibits the reported behavior
        Assertions.assertTrue(
            event.body().contains(expectedUrl),
            "Slack body must contain the URL: " + expectedUrl + ". Actual body: " + event.body()
        );
    }
}
