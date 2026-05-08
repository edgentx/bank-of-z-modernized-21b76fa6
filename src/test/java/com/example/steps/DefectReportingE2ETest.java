package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectLinkedEvent;
import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.InMemorySlackNotificationPort;
import com.example.services.DefectReportingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class DefectReportingE2ETest {

    @Autowired
    private DefectReportingService service;

    @Autowired
    private InMemorySlackNotificationPort slackPort;

    @Autowired
    private DefectRepository repository;

    private String currentDefectId;

    @Given("a defect exists with title {string}")
    public void a_defect_exists_with_title(String title) {
        // Setup step if needed, though the reporting flow creates the aggregate
    }

    @When("the defect is reported via temporal-worker exec")
    public void the_defect_is_reported_via_temporal_worker_exec() {
        // This simulates the temporal worker triggering the report_defect flow
        currentDefectId = service.reportDefect("VW-454: GitHub URL missing", "Slack body does not contain the issue link.");
    }

    @Then("the Slack body contains GitHub issue link")
    public void the_slack_body_contains_github_issue_link() {
        // Verify the aggregate state
        DefectAggregate aggregate = repository.findById(currentDefectId).orElseThrow();
        assertNotNull(aggregate.getGithubUrl());
        assertTrue(aggregate.getGithubUrl().startsWith("https://github.com/"));

        // Verify the external system (Slack Mock) received the correct payload
        assertTrue(slackPort.containsUrl(aggregate.getGithubUrl()), 
            "Slack message should contain the GitHub URL: " + aggregate.getGithubUrl());
    }
}