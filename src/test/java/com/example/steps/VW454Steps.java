package com.example.steps;

import com.example.adapters.GitHubRestAdapter;
import com.example.adapters.OkHttpSlackClient;
import com.example.activities.DefectReportingActivities;
import com.example.activities.DefectReportingActivitiesImpl;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Steps for validating VW-454: Slack body contains GitHub URL.
 * Tests in RED phase — implementation expected to fail initially.
 */
public class VW454Steps {

    @Autowired
    private OkHttpSlackClient slackClient;

    @Autowired
    private GitHubRestAdapter gitHubAdapter;

    private String reportedIssueUrl;
    private Exception workflowException;

    @Given("the GitHub issue is created successfully")
    public void the_git_hub_issue_is_created_successfully() {
        // Configure the mock adapter to return a valid URL
        // This simulates the successful creation of an issue in GitHub
        String mockUrl = "https://github.com/microsoft/EGDCrypto-Bank-of-Z/issues/454";
        when(gitHubAdapter.createIssue(any(), any(), any())).thenReturn(mockUrl);
    }

    @When("the defect reporting workflow is executed")
    public void the_defect_reporting_workflow_is_executed() {
        try {
            // Trigger the temporal activity or use the adapter directly to simulate the workflow
            // Note: We are verifying the integration point described in VW-454
            DefectReportingActivities activities = new DefectReportingActivitiesImpl(slackClient, gitHubAdapter);
            activities.reportDefect("VW-454", "Test Defect", "Description");
        } catch (Exception e) {
            workflowException = e;
        }
    }

    @Then("the Slack notification body includes the GitHub issue URL")
    public void the_slack_notification_body_includes_the_git_hub_issue_url() {
        // Verify that the Slack client was called
        verify(slackClient, atLeastOnce()).publish(any());

        // Verify that the specific URL string was part of the payload sent to Slack
        // This captures the requirement: "Slack body includes GitHub issue: <url>"
        verify(slackClient).publish(argThat(payload -> 
            payload != null && 
            payload.contains("https://github.com/microsoft/EGDCrypto-Bank-of-Z/issues/454")
        ));
    }
}
