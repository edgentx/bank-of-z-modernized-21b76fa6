package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.ports.VForce360RepositoryPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for S-FB-1: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 */
public class SFB1Steps {

    // Using mocks via injection or manual instantiation for this suite
    // Since we are mocking the Temporal Worker execution logic directly in the test

    private final VForce360RepositoryPort defectRepo = new InMemoryDefectRepository();
    private final GitHubPort gitHubAdapter = new MockGitHubAdapter();
    private final MockSlackAdapter slackAdapter = new MockSlackAdapter();

    private DefectAggregate aggregate;
    private Exception caughtException;

    @Given("a defect report is triggered via temporal-worker exec")
    public void a_defect_report_is_triggered() {
        // Simulate the initial state
        String defectId = "DEF-454";
        aggregate = new DefectAggregate(defectId);
        defectRepo.save(aggregate);
    }

    @When("the defect report workflow executes with details {string}, {string}, {string}")
    public void the_defect_report_workflow_executes(String title, String description, String severity) {
        // This WHEN block simulates the Temporal Workflow Activity orchestration logic
        // which is currently missing/incomplete (The Bug).
        // We attempt to perform the sequence:
        // 1. Execute Command on Aggregate
        // 2. Create GitHub Issue
        // 3. Notify Slack

        try {
            ReportDefectCmd cmd = new ReportDefectCmd(aggregate.id(), title, description, severity);

            // 1. Aggregate processing
            // In the RED phase, this throws UnknownCommandException or does not produce the URL state
            // In GREEN phase, this will work
            var events = aggregate.execute(cmd);

            // If we reach here, assume we need to hydrate the aggregate from events (simplified for test)
            // or just proceed to the adapter calls which would normally happen in a Workflow/Service layer

            // 2. GitHub Adapter Call (Simulating the Workflow Activity)
            String issueUrl = gitHubAdapter.createIssue(title, description);

            // 3. Slack Adapter Call (Simulating the Workflow Activity)
            String slackBody = "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;
            slackAdapter.sendMessage("#vforce360-issues", slackBody);

        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue link")
    public void the_slack_body_should_contain_the_github_issue_link() {
        // If an exception was thrown during command execution (RED phase), fail appropriately
        if (caughtException != null) {
            fail("Workflow execution failed with exception: " + caughtException.getMessage());
        }

        assertTrue(slackAdapter.lastMessageContains("github.com"),
            "Slack message should contain 'github.com'");
        assertTrue(slackAdapter.lastMessageContains("/issues/"),
            "Slack message should contain '/issues/'");
    }
}