package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd Feature
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Default to a valid state for the happy path
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // By default, a new aggregate is NOT authenticated (constructor sets isAuthenticated = false)
        // So we don't need to do anything else, it's already in the violating state.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markAuthenticated(); // Ensure auth is valid so we isolate the timeout failure
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated();
        aggregate.invalidateNavigationState();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // This step ensures we know the ID to use in the When step
        // We store it in scenario context if needed, but here we hardcode valid values for simplicity
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Same as tellerId
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd("teller-1", "terminal-1");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Result events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("teller-1", startedEvent.tellerId());
        assertEquals("terminal-1", startedEvent.terminalId());
        assertEquals("session-123", startedEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException");
        
        // Verify no events were emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on failure");
    }
}
