package com.example.steps;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.SlackMessageValidator;
import com.example.infrastructure.TemporalActivities;
import com.example.infrastructure.TemporalActivitiesImpl;
import com.example.mocks.InMemoryDefectRepository;
import com.example.ports.SlackNotifier;
import com.example.services.DefectReportingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class SFB1DefectReportingSteps {

    private InMemoryDefectRepository repository;
    private MockSlackNotifier slackNotifier;
    private DefectReportingService service;
    private TemporalActivities temporalActivities;
    private Exception capturedException;
    private String result;

    static class MockSlackNotifier implements SlackNotifier {
        String lastChannel;
        String lastMessage;
        boolean called = false;

        @Override
        public void send(String channel, String message) {
            this.lastChannel = channel;
            this.lastMessage = message;
            this.called = true;
        }
    }

    @Given("the defect reporting system is initialized")
    public void the_defect_reporting_system_is_initialized() {
        repository = new InMemoryDefectRepository();
        slackNotifier = new MockSlackNotifier();
        service = new DefectReportingService(repository, slackNotifier);
        temporalActivities = new TemporalActivitiesImpl(slackNotifier, repository);
    }

    @Given("a valid defect command exists for VW-454")
    public void a_valid_defect_command_exists_for_vw_454() {
        // Setup step, command creation happens in 'When'
    }

    @When("the defect report_defect command is triggered via temporal-worker exec")
    public void the_defect_report_defect_command_is_triggered_via_temporal_worker_exec() {
        try {
            // Simulating the processing of a defect report
            DefectAggregate aggregate = new DefectAggregate("DEF-454");
            ReportDefectCmd cmd = new ReportDefectCmd(
                "DEF-454",
                "Fix: Validating VW-454 — GitHub URL in Slack body",
                "End-to-end validation failure",
                "LOW",
                "validation",
                "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
                "https://github.com/example/bank-of-z/issues/454"
            );
            
            // Execute command on aggregate
            aggregate.execute(cmd);
            
            // Service orchestration
            service.reportDefect(aggregate);
            
            // Activity execution simulation
            result = temporalActivities.reportDefectActivity("dummy-json");
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the Slack body contains GitHub issue link")
    public void the_slack_body_contains_github_issue_link() {
        // Assert Service Level
        assertTrue(slackNotifier.called, "Slack notifier should have been called");
        assertEquals("#vforce360-issues", slackNotifier.lastChannel);
        assertTrue(SlackMessageValidator.containsGitHubLink(slackNotifier.lastMessage), 
            "Slack body must contain GitHub URL. Got: " + slackNotifier.lastMessage);
        
        // Assert Activity Level
        assertNotNull(result);
        assertNull(capturedException, "No exception should have occurred: " + (capturedException != null ? capturedException.getMessage() : ""));
    }
}