package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // We assume the command we construct later will not have auth,
        // or the aggregate state itself tracks auth. Based on the pattern,
        // we'll simulate an unauthenticated attempt.
        // For simplicity in this phase, we might rely on Command validation.
        // However, let's assume the aggregate needs to be in a specific state
        // or the command needs to be invalid. Let's make the command invalid in the When step.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // In a real scenario, this might mean checking an existing active session's timeout.
        // For the Start command, this might mean requesting a timeout of 0.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
        // This implies invalid context data in the command.
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context handled in execution step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context handled in execution step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        executeCommand("teller-123", "term-ABC", Duration.ofHours(8), true, "CONTEXT_MAIN_MENU");
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_with_invalid_auth() {
        executeCommand("teller-123", "term-ABC", Duration.ofHours(8), false, "CONTEXT_MAIN_MENU");
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_StartSessionCmd_command_is_executed_with_invalid_timeout() {
        executeCommand("teller-123", "term-ABC", Duration.ZERO, true, "CONTEXT_MAIN_MENU");
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_StartSessionCmd_command_is_executed_with_invalid_navigation_state() {
        executeCommand("teller-123", "term-ABC", Duration.ofHours(8), true, "INVALID_CONTEXT");
    }

    private void executeCommand(String tellerId, String terminalId, Duration timeout, boolean isAuthenticated, String navContext) {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId, timeout, isAuthenticated, navContext);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
