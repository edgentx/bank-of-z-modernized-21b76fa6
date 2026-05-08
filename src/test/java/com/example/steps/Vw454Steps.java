package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.adapters.WebhookSlackNotificationAdapter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Steps for validating VW-454: GitHub URL in Slack body.
 * This acts as the regression test for the defect.
 */
public class Vw454Steps {

    private DefectAggregate defectAggregate;
    private ReportDefectCmd cmd;
    private final DefectRepository defectRepository = new InMemoryDefectRepository();
    private final TestSlackValidator slackValidator = new TestSlackValidator();
    private final WebhookSlackNotificationAdapter slackAdapter = new WebhookSlackNotificationAdapter(slackValidator);
    
    private Exception capturedException;

    @Given("a defect report command is issued")
    public void a_defect_report_command_is_issued() {
        // Setup defect ID corresponding to VW-454 logic
        String defectId = "VW-454";
        this.cmd = new ReportDefectCmd(defectId, "GitHub URL Missing", "Slack body does not contain link", "LOW");
        this.defectAggregate = new DefectAggregate(defectId);
    }

    @When("the defect is reported and Slack notification is triggered")
    public void the_defect_is_reported_and_slack_notification_is_triggered() {
        try {
            // 1. Execute Domain Logic (Report Defect)
            var events = defectAggregate.execute(cmd);
            defectRepository.save(defectAggregate);

            // 2. Prepare Slack Body (simulating the temporal-worker exec flow)
            if (!events.isEmpty()) {
                String githubUrl = ((com.example.domain.defect.model.DefectReportedEvent) events.get(0)).githubIssueUrl();
                String slackBody = "Defect Reported: " + githubUrl;

                // 3. Trigger Slack Adapter
                slackAdapter.post(slackBody);
            }
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("the Slack body should include the GitHub issue link")
    public void the_slack_body_should_include_the_github_issue_link() {
        // If the validator logic is broken or missing, this might fail
        assertNull(capturedException, "Slack notification should succeed with GitHub URL");
        assertTrue(slackValidator.lastBodyValidated.contains("github.com"), "Body should contain github.com");
    }

    @When("the Slack body is missing the GitHub URL")
    public void the_slack_body_is_missing_the_github_url() {
        String invalidBody = "Defect Reported without link";
        try {
            slackAdapter.post(invalidBody);
        } catch (IllegalArgumentException e) {
            this.capturedException = e;
        }
    }

    @Then("the validation should fail with an error")
    public void the_validation_should_fail_with_an_error() {
        assertNotNull(capturedException, "Expected validation exception");
        assertEquals("Slack body validation failed: GitHub URL missing", capturedException.getMessage());
    }

    // --- Mocks / Support Classes ---

    static class InMemoryDefectRepository implements DefectRepository {
        private final java.util.Map<String, DefectAggregate> store = new java.util.HashMap<>();
        @Override public void save(DefectAggregate defect) { store.put(defect.id(), defect); }
        @Override public DefectAggregate findById(String defectId) { return store.get(defectId); }
    }

    static class TestSlackValidator implements com.example.domain.shared.SlackMessageValidator {
        String lastBodyValidated;
        @Override public boolean containsGitHubUrl(String body) {
            lastBodyValidated = body;
            return body != null && body.contains("github.com");
        }
    }
}
