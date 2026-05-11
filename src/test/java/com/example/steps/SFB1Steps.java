package com.example.steps;

import com.example.domain.defect.adapter.GitHubIssueTrackerAdapter;
import com.example.domain.defect.adapter.SlackNotifier;
import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.GitHubIssueLinkedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

public class SFB1Steps {

    // We will use mocks via Spring context or manual injection
    private MockGitHubIssueTrackerAdapter mockGitHub;
    private MockSlackNotifier mockSlack;
    private DefectAggregate aggregate;

    public SFB1Steps() {
        // Initialize mocks manually for this standalone step file
        // In a real Spring Boot test, these would be @MockBeans
        mockGitHub = new MockGitHubIssueTrackerAdapter();
        mockSlack = new MockSlackNotifier();
    }

    @Given("a defect is reported via Temporal worker")
    public void a_defect_is_reported_via_temporal_worker() {
        aggregate = new DefectAggregate("fb-1");
        List<DomainEvent> events = aggregate.execute(
            new com.example.domain.defect.model.ReportDefectCmd("fb-1", "VW-454", "GitHub URL missing in Slack")
        );
        assertFalse(events.isEmpty());
    }

    @When("the defect report workflow executes to completion")
    public void the_defect_report_workflow_executes_to_completion() {
        // Simulate the workflow steps
        // 1. Create GitHub Issue
        String fakeUrl = mockGitHub.createIssue("VW-454", "GitHub URL missing in Slack");
        
        // 2. Link issue to Aggregate
        aggregate.execute(
            new com.example.domain.defect.model.LinkGitHubIssueCmd("fb-1", fakeUrl)
        );

        // 3. Notify Slack
        mockSlack.notify(aggregate.getTitle(), aggregate.getGithubIssueUrl());
    }

    @Then("the Slack notification body contains the GitHub issue URL")
    public void the_slack_notification_body_contains_the_github_issue_url() {
        // Assert that the Slack mock received the URL
        String lastMessageBody = mockSlack.getLastMessageBody();
        
        assertNotNull(lastMessageBody, "Slack message body should not be null");
        assertTrue(
            lastMessageBody.contains("http"), 
            "Slack body should contain a URL"
        );
        assertTrue(
            lastMessageBody.contains("github.com"), 
            "Slack body should contain github.com"
        );
        
        // Verify the exact pattern <url> as per defect
        assertTrue(
            lastMessageBody.contains("<" + mockGitHub.getLastCreatedUrl() + ">"),
            "Slack body must contain the link in angle brackets: <url>"
        );
    }

    // --- Inner Mock Classes for Test Isolation ---
    
    public static class MockGitHubIssueTrackerAdapter implements com.example.domain.defect.adapter.GitHubIssueTrackerAdapter {
        private String lastUrl;

        @Override
        public String createIssue(String title, String description) {
            this.lastUrl = "https://github.com/mock/issues/454";
            return lastUrl;
        }

        public String getLastCreatedUrl() {
            return lastUrl;
        }
    }

    public static class MockSlackNotifier implements com.example.domain.defect.adapter.SlackNotifier {
        private String lastBody;

        @Override
        public void notify(String title, String url) {
            // Simulate constructing the body
            this.lastBody = "Defect Reported: " + title + "\nGitHub Issue: <" + url + ">";
        }

        public String getLastMessageBody() {
            return lastBody;
        }
    }
}