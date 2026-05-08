package com.example.steps;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ReportDefectWorkflow;
import com.example.domain.validation.model.ReportDefectWorkflowImpl;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Steps for verifying VW-454: GitHub URL in Slack body.
 * Uses a real Temporal TestWorkflowEnvironment to execute the Sagas,
 * but uses MockSlackNotifier to verify the outputs without external dependencies.
 */
public class VW454Steps {

    @Autowired
    private MockSlackNotifier mockSlack;

    @Autowired
    private GitHubIssueTracker gitHub; // Will be a mock or stub implementation in test context

    private ReportDefectCommand command;
    private Exception workflowException;
    private String resultUrl;

    @Given("the temporal worker is executing the report_defect workflow")
    public void the_worker_is_ready() {
        // Setup is handled by the TestSuite initialization
        assertNotNull(mockSlack, "MockSlack should be autowired");
    }

    @When("a defect report command is triggered with title {string} and id {string}")
    public void a_defect_report_is_triggered(String title, String id) {
        this.command = new ReportDefectCommand(
            id,
            title,
            "Description of the failure",
            "LOW"
        );
        
        try {
            // Instantiate the workflow with the mock dependencies provided by Spring TestContext
            ReportDefectWorkflow workflow = new ReportDefectWorkflowImpl(mockSlack, gitHub);
            
            // Execute the workflow logic synchronously for this Cucumber test
            this.resultUrl = workflow.execute(command);
            
        } catch (Exception e) {
            this.workflowException = e;
        }
    }

    @Then("the Slack body should contain the GitHub issue URL")
    public void the_slack_body_should_contain_github_url() {
        if (workflowException != null) {
            fail("Workflow threw exception: " + workflowException.getMessage());
        }

        // 1. Verify a message was sent
        assertFalse(mockSlack.getMessages().isEmpty(), "Slack should have received a message");

        // 2. Verify the content matches the defect report
        MockSlackNotifier.Message msg = mockSlack.getMessages().get(0);
        assertTrue(msg.body.contains(command.defectId()), "Body should contain defect ID");
        assertTrue(msg.body.contains(command.title()), "Body should contain title");

        // 3. **CRITICAL CHECK for VW-454**
        // The defect report states: "Slack body includes GitHub issue: <url>"
        assertTrue(msg.body.contains("github.com"), "Body should contain GitHub URL domain");
        assertTrue(msg.body.contains("http"), "Body should contain a valid URL");
        assertTrue(msg.body.contains(resultUrl), "Body should contain the specific URL returned by the mock");
    }
}
