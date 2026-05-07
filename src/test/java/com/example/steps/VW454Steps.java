package com.example.steps;

import com.example.application.DefectReportingService;
import com.example.domain.shared.Command;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Repository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VW454Steps {

    @Autowired
    private DefectReportingService service;

    @Autowired
    private GitHubPort gitHubPort;

    @Autowired
    private SlackNotificationPort slackNotificationPort;

    @Autowired
    private VForce360Repository repository;

    @Given("the system is ready to report defects")
    public void the_system_is_ready() {
        // NOP - Spring context initializes mocks
    }

    @When("the defect report is triggered with title {string} and body {string}")
    public void the_defect_report_is_triggered(String title, String body) {
        when(gitHubPort.createIssue(any(), any(), any())).thenReturn("https://github.com/test/issues/454");
        
        ReportDefectCmd cmd = new ReportDefectCmd(title, body, "VForce360", "LOW");
        service.reportDefect(cmd);
    }

    @Then("the Slack notification body should contain the GitHub issue URL")
    public void the_slack_notification_body_should_contain_the_github_url() {
        // Verify that the sendNotification method was called with a string containing the URL
        verify(slackNotificationPort).sendNotification(contains("https://github.com/test/issues/454"));
    }

    @Then("the Slack notification body should not be null")
    public void the_slack_notification_body_should_not_be_null() {
        verify(slackNotificationPort).sendNotification(any());
    }
}
