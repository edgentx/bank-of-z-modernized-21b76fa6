package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    // Test Context
    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a session that is valid: Active, Authenticated, HOME, Not timed out
        aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        aggregate.markAuthenticated("teller-01"); // Simulate successful login
        aggregate.updateNavigation("HOME");       // Simulate being at home
        aggregate.heartbeat();                     // Simulate recent activity
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Normally the command is constructed by the handler/controller
        // Here we construct it for the test
        command = new EndSessionCmd("session-123", "teller-01");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Create session but DO NOT authenticate
        aggregate = new TellerSessionAggregate("session-unauth", Duration.ofMinutes(15));
        aggregate.updateNavigation("HOME");
        // Intentionally missing: markAuthenticated(...)
        command = new EndSessionCmd("session-unauth", "teller-01");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Create session with a very short timeout
        aggregate = new TellerSessionAggregate("session-timeout", Duration.ofMillis(10));
        aggregate.markAuthenticated("teller-01");
        aggregate.updateNavigation("HOME");
        
        // Wait longer than timeout to ensure stale state
        try {
            Thread.sleep(50); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        command = new EndSessionCmd("session-timeout", "teller-01");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Create a valid session but move it to a context where ending is invalid (e.g. deep in a transaction)
        aggregate = new TellerSessionAggregate("session-nav-error", Duration.ofMinutes(15));
        aggregate.markAuthenticated("teller-01");
        aggregate.updateNavigation("CASH_DEPOSIT_INPUT"); // Not HOME
        
        command = new EndSessionCmd("session-nav-error", "teller-01");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "Events should not be empty");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", event.type());
        
        // Verify aggregate state mutation
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
        assertFalse(aggregate.isAuthenticated(), "Aggregate should not be authenticated");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Exception should have been thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
        
        // Verify no event was emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on failure");
    }
}
