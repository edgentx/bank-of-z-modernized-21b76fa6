package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidateUrlPresenceCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ValidationPassedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class SFB1Steps {

    private DefectAggregate defectAggregate;
    private ValidationAggregate validationAggregate;
    private String actualSlackBody;
    private String expectedGithubUrl;
    private Exception capturedException;

    // Scenario: Successfully report a defect with a GitHub URL
    @Given("a defect is reported with title {string} and GitHub URL {string}")
    public void a_defect_is_reported_with_title_and_github_url(String title, String url) {
        String id = "DEFECT-" + System.currentTimeMillis();
        this.defectAggregate = new DefectAggregate(id);
        this.expectedGithubUrl = url;

        // Execute command
        ReportDefectCommand cmd = new ReportDefectCommand(id, title, "Defect description", url);
        defectAggregate.execute(cmd);
    }

    @When("the defect is stored")
    public void the_defect_is_stored() {
        // In a real application, this would persist via repository.
        // For Red Phase, we rely on the aggregate state change.
        Assertions.assertNotNull(defectAggregate.getTitle());
    }

    @Then("the defect aggregate should contain the GitHub URL {string}")
    public void the_defect_aggregate_should_contain_the_github_url(String url) {
        Assertions.assertEquals(url, defectAggregate.getGithubUrl());
    }

    @Then("a DefectReportedEvent should be emitted containing the URL")
    public void a_defect_reported_event_should_be_emitted_containing_the_url() {
        List<com.example.domain.shared.DomainEvent> events = defectAggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty());
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        Assertions.assertEquals(expectedGithubUrl, event.githubUrl());
    }

    // Scenario: Validate that the Slack Body contains the GitHub URL
    @Given("a defect exists with GitHub URL {string}")
    public void a_defect_exists_with_github_url(String url) {
        this.expectedGithubUrl = url;
        // Setup defect aggregate to represent the state
        String id = "DEFECT-VAL-1";
        this.defectAggregate = new DefectAggregate(id);
        this.defectAggregate.execute(new ReportDefectCommand(id, "Val Title", "Desc", url));
    }

    @Given("the Slack notification body is generated containing the URL")
    public void the_slack_notification_body_is_generated_containing_the_url() {
        // Simulating the body generation logic which should include the link
        this.actualSlackBody = "Issue reported: " + expectedGithubUrl;
    }

    @When("the validation workflow checks the body for the link")
    public void the_validation_workflow_checks_the_body_for_the_link() {
        this.validationAggregate = new ValidationAggregate("VAL-1");
        ValidateUrlPresenceCommand cmd = new ValidateUrlPresenceCommand("VAL-1", expectedGithubUrl, actualSlackBody);
        validationAggregate.execute(cmd);
    }

    @Then("the validation should pass successfully")
    public void the_validation_should_pass_successfully() {
        Assertions.assertTrue(validationAggregate.isPassed());
        
        List<com.example.domain.shared.DomainEvent> events = validationAggregate.uncommittedEvents();
        ValidationPassedEvent event = (ValidationPassedEvent) events.get(0);
        Assertions.assertTrue(event.passed());
    }

    // Negative Scenario: Validation fails if URL is missing
    @Given("the Slack notification body is generated but missing the URL")
    public void the_slack_notification_body_is_generated_but_missing_the_url() {
        this.actualSlackBody = "Issue reported (link missing)";
    }

    @When("the validation workflow runs")
    public void the_validation_workflow_runs() {
        this.validationAggregate = new ValidationAggregate("VAL-2");
        // Using a dummy expected URL
        ValidateUrlPresenceCommand cmd = new ValidateUrlPresenceCommand("VAL-2", "http://github.com/issue/1", actualSlackBody);
        validationAggregate.execute(cmd);
    }

    @Then("the validation should fail")
    public void the_validation_should_fail() {
        Assertions.assertFalse(validationAggregate.isPassed());
        
        List<com.example.domain.shared.DomainEvent> events = validationAggregate.uncommittedEvents();
        ValidationPassedEvent event = (ValidationPassedEvent) events.get(0);
        Assertions.assertFalse(event.passed());
    }
}
