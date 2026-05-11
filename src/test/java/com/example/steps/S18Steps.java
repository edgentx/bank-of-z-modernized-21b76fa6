package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // ID is arbitrary for a new aggregate
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate that the aggregate was loaded/authenticated in a valid state
        // In this domain implementation, we assume the aggregate is created
        // or rehydrated in a state allowing StartSession if state is IDLE
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
        // Use default valid parameters for the success case
        executeCommand("teller-1", "term-1", Duration.ofHours(8), TellerSessionState.ROOT_MENU);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("teller-1", event.tellerId());
        Assertions.assertEquals("term-1", event.terminalId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We simulate this by passing null tellerId
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // We simulate this by passing a negative duration (invalid)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // We simulate this by passing null state
    }

    // Helper to execute command with specific params to trigger failures
    private void executeCommand(String tellerId, String terminalId, Duration timeout, TellerSessionState state) {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-auth-fail".equals(aggregate.id()) ? null : tellerId,
                    terminalId, timeout, state);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the StartSessionCmd command is executed on the violating aggregate")
    public void the_StartSessionCmd_command_is_executed_on_violating_aggregate() {
        // Dispatch to specific violation helpers based on the aggregate ID used in Given
        String id = aggregate.id();
        
        if ("session-auth-fail".equals(id)) {
            executeCommand(null, "term-1", Duration.ofHours(8), TellerSessionState.ROOT_MENU);
        } else if ("session-timeout-fail".equals(id)) {
            executeCommand("teller-1", "term-1", Duration.ofSeconds(-1), TellerSessionState.ROOT_MENU);
        } else if ("session-nav-fail".equals(id)) {
            executeCommand("teller-1", "term-1", Duration.ofHours(8), null);
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException,
            "Expected domain error (IllegalStateException or IllegalArgumentException)");
    }

}
