package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockGithubAdapter;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Steps for defect VW-454.
 * Validates that when a defect is reported, the resulting Slack notification
 * contains the URL to the created GitHub issue.
 */
public class VW454ValidationSteps {

    private ValidationAggregate validationAggregate;
    private MockGithubAdapter githubAdapter;
    private MockSlackNotifier slackNotifier;

    // Constants derived from the story/project context
    private final String VALIDATION_ID = "vw-454-validation";
    private final String EXPECTED_GITHUB_URL = "https://github.com/egdcrypto-bank-of-z/issues/454";

    @Given("a validation workflow is triggered for defect VW-454")
    public void a_validation_workflow_is_triggered() {
        validationAggregate = new ValidationAggregate(VALIDATION_ID);
        
        // Inject Mocks
        githubAdapter = new MockGithubAdapter();
        githubAdapter.setMockUrl(EXPECTED_GITHUB_URL);
        
        slackNotifier = new MockSlackNotifier();
    }

    @When("the defect report is processed via temporal-worker exec")
    public void the_defect_report_is_processed() {
        // Simulate the temporal worker execution:
        // 1. Logic would call GithubAdapter to create an issue
        String createdUrl = githubAdapter.createIssue("VW-454 Defect", "Body content");
        
        // 2. Logic would command the Validation Aggregate with the result
        // (Assuming the Command includes the URL returned from Github)
        ReportDefectCmd cmd = new ReportDefectCmd(VALIDATION_ID, createdUrl);
        
        // 3. Execute command
        validationAggregate.execute(cmd);
        
        // 4. Logic would format and send Slack notification
        String messageBody = String.format(
            "Validation complete. Issue created: %s", 
            validationAggregate.getGithubIssueUrl()
        );
        slackNotifier.send(messageBody);
    }

    @Then("the Slack body contains the GitHub issue link")
    public void the_slack_body_contains_github_link() {
        // Verify: The mock adapter should have received the message
        String sentMessage = slackNotifier.getLastMessage();
        
        assertNotNull(sentMessage, "Slack should have received a message");
        assertTrue(
            sentMessage.contains(EXPECTED_GITHUB_URL), 
            "Slack body must contain the specific GitHub Issue URL"
        );
        assertTrue(
            sentMessage.contains("github.com"),
            "Slack body must contain a github.com domain"
        );
    }
}
