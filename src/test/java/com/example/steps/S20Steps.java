package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate session;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // --- Repository Stub for context (if needed for persistence simulation, though we construct Aggregates directly) ---
    // We focus on the Aggregate logic for unit testing behavior.

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a session that is fully valid and active
        String id = "session-123";
        session = new TellerSessionAggregate(id);
        // Simulate that the session has been started via an event
        // For this test, we construct the aggregate in a valid state manually or via a 'Start' command if it existed.
        // Here we assume the constructor creates a raw aggregate, and we configure it to be valid.
        // Valid State: Authenticated, Active, Not Timed out, Valid Navigation.
        // Note: In a real scenario, we might load from repo. Here we instantiate directly.
        // We rely on the aggregate having a way to be 'active'.
        // Let's assume the constructor creates a session in a state ready to receive commands,
        // OR we need to hydrate it. Given the constraints, I'll assume the Constructor sets the ID,
        // and we can test the 'End' logic.
        
        // However, to test "Successfully End", the session must exist.
        // The error logs show it was looking in 'domain.tellermemory', but the Root instruction says 'domain/tellersession'.
        // I will assume the TellerSessionAggregate is placed in com.example.domain.tellersession.model.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate creation in the previous step
        assertNotNull(session.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(session.id(), "TellerLogout");
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(session.id(), event.aggregateId());
        assertEquals("session.ended", event.type());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // To test ending a session that isn't authenticated, we can create an aggregate in a weird state
        // or rely on the logic that you can't end what isn't started.
        // However, the prompt says "EndSessionCmd rejected".
        // We will simulate a session that is NOT authenticated.
        // I'll add a mechanism to the aggregate or constructor to force this state for testing purposes,
        // or (better) assume the Aggregate protects itself.
        // Since I control the aggregate code, I will make the aggregate check an 'authenticated' flag.
        session = new TellerSessionAggregate("unauth-session");
        session.setAuthenticated(false); // Method I'll add to aggregate for testing/internal state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        session = new TellerSessionAggregate("timeout-session");
        session.setLastActivityTimestamp(Instant.now().minus(Duration.ofHours(2))); // Timed out
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        session = new TellerSessionAggregate("nav-error-session");
        session.setNavigationStateConsistent(false); // Method I'll add to aggregate
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, 
            "Expected a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
