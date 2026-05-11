package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.GitHubIssueLinkedEvent;
import com.example.domain.defect.model.LinkGitHubIssueCmd;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.InMemoryDefectRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class SFB1Steps {

    private final DefectRepository repository = new InMemoryDefectRepository();
    private DefectAggregate aggregate;
    private Exception capturedException;

    // Common Background
    @Given("a defect report command exists")
    public void a_defect_report_command_exists() {
        // Setup command data
    }

    // Scenario 1: Reporting
    @When("the defect report is executed")
    public void the_defect_report_is_executed() {
        try {
            aggregate = new DefectAggregate("defect-1");
            var cmd = new ReportDefectCmd("defect-1", "Validation fails", "Body missing URL", "LOW");
            aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the defect should be saved with reported status")
    public void the_defect_should_be_saved_with_reported_status() {
        assertNotNull(aggregate);
        assertEquals(1, aggregate.uncommittedEvents().size());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof DefectReportedEvent);
        assertNotNull(repository.findById("defect-1"));
    }

    // Scenario 2: Linking GitHub
    @Given("a defect has been reported")
    public void a_defect_has_been_reported() {
        aggregate = new DefectAggregate("defect-2");
        aggregate.execute(new ReportDefectCmd("defect-2", "Slack Integration", "Missing URL", "LOW"));
        aggregate.clearEvents(); // Clear domain events from previous step
    }

    @When("a GitHub issue link command is executed with URL {string}")
    public void a_github_issue_link_command_is_executed_with_url(String url) {
        try {
            aggregate.execute(new LinkGitHubIssueCmd("defect-2", url));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the aggregate should contain the GitHub URL")
    public void the_aggregate_should_contain_the_github_url() {
        assertEquals("https://github.com/org/repo/issues/1", aggregate.getGithubUrl());
    }

    @Then("a GitHubIssueLinkedEvent should be emitted")
    public void a_github_issue_linked_event_should_be_emitted() {
        assertEquals(1, aggregate.uncommittedEvents().size());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof GitHubIssueLinkedEvent);
        GitHubIssueLinkedEvent event = (GitHubIssueLinkedEvent) aggregate.uncommittedEvents().get(0);
        assertEquals("https://github.com/org/repo/issues/1", event.url());
    }

    // Scenario 3: Negative Validation
    @Given("a defect exists")
    public void a_defect_exists() {
        aggregate = new DefectAggregate("defect-3");
        aggregate.execute(new ReportDefectCmd("defect-3", "Bad URL", "Testing invalid URL", "LOW"));
        aggregate.clearEvents();
    }

    @When("the system tries to link an invalid GitHub URL {string}")
    public void the_system_tries_to_link_an_invalid_github_url(String url) {
        try {
            aggregate.execute(new LinkGitHubIssueCmd("defect-3", url));
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command should fail with an error")
    public void the_command_should_fail_with_an_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("Valid GitHub URL required"));
    }

    @Then("no GitHub issue event should be emitted")
    public void no_github_issue_event_should_be_emitted() {
        assertTrue(aggregate.uncommittedEvents().isEmpty());
        assertNull(aggregate.getGithubUrl());
    }
}