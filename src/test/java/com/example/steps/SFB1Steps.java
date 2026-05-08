package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.mocks.InMemoryValidationRepository;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-FB-1: Validating VW-454.
 * Simulates the temporal-worker exec flow.
 */
public class SFB1Steps {

    private ValidationRepository repository = new InMemoryValidationRepository();
    private MockSlackNotificationPort slackPort = new MockSlackNotificationPort();
    private Exception capturedException;
    private String defectId;

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered_via_temporal_worker_exec() {
        // Initialize the aggregate
        ValidationAggregate aggregate = repository.create();
        this.defectId = aggregate.id();
    }

    @When("the defect report contains a valid GitHub URL")
    public void the_defect_report_contains_a_valid_github_url() {
        try {
            ValidationAggregate aggregate = repository.findById(defectId).orElseThrow();
            String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
            ReportDefectCmd cmd = new ReportDefectCmd(defectId, "Validation failed", githubUrl);
            
            // Execute command
            aggregate.execute(cmd);
            repository.save(aggregate);

            // Trigger notification (simulating the workflow)
            aggregate.uncommittedEvents().forEach(event -> {
                if (event instanceof com.example.domain.validation.model.DefectReportedEvent e) {
                    slackPort.notify(e);
                }
            });
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body includes the GitHub issue link")
    public void the_slack_body_includes_the_github_issue_link() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertTrue(slackPort.wasUrlIncludedInLastMessage("https://github.com/egdcrypto/bank-of-z/issues/454"),
            "Slack body should contain the GitHub URL");
    }
}
