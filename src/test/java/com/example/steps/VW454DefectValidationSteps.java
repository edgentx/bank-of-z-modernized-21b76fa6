package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.mocks.MockSlackNotificationPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cucumber Steps for validating VW-454.
 * Ensures that when a defect is reported via the temporal worker,
 * the resulting Slack message contains the GitHub issue URL.
 */
public class VW454DefectValidationSteps {

    private MockSlackNotificationPort mockSlack;
    private DefectAggregate aggregate;
    private String defectId;

    @Given("a defect reporting workflow is initialized")
    public void a_defect_reporting_workflow_is_initialized() {
        mockSlack = new MockSlackNotificationPort();
        defectId = "VW-454";
        // We initialize the aggregate with the mock port
        aggregate = new DefectAggregate(defectId, mockSlack);
    }

    @When("the temporal worker triggers _report_defect command")
    public void the_temporal_worker_triggers_report_defect_command() {
        ReportDefectCommand cmd = new ReportDefectCommand(
                defectId,
                "GitHub URL in Slack body (end-to-end)",
                "Defect reported by user.",
                "LOW",
                "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // Execute the command logic
        aggregate.execute(cmd);
    }

    @Then("the Slack body includes the GitHub issue URL")
    public void the_slack_body_includes_the_github_issue_url() {
        // 1. Verify the Mock captured the message
        assertThat(mockSlack.getMessages()).isNotEmpty();

        // 2. Verify the content
        MockSlackNotificationPort.SentMessage msg = mockSlack.getLastMessage();
        assertThat(msg.channel).isEqualTo("#vforce360-issues");

        // 3. Verify the GitHub URL is present (Regression check for VW-454)
        // Expected URL format based on the defect description
        assertThat(msg.body).contains("https://github.com/mock-repo/issues/" + defectId);
    }

    @Then("the validation no longer exhibits the reported behavior")
    public void the_validation_no_longer_exhibits_the_reported_behavior() {
        // This confirms the fix. If the URL is missing, this assertion fails.
        MockSlackNotificationPort.SentMessage msg = mockSlack.getLastMessage();
        
        // Basic check that the body isn't just text without the link
        assertThat(msg.body).isNotBlank();
        assertThat(msg.body).contains("http"); 
    }
}
