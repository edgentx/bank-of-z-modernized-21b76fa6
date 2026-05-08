package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "TS-123";
        // Create aggregate in a valid state (Active, Authenticated)
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate previous initialization state manually for BDD context
        // In a real lifecycle, we would hydrate from InitSessionCmd events
        // But given the constraints and the limited scope of S-20, we construct a valid state object.
        // We use reflection or package-private setup if available, or just trust the constructor/default state
        // Assuming the constructor creates an object that can be manipulated to a valid state.
        // For this test, we assume the constructor creates a fresh session.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID handled in aggregate construction, verify here
        assertNotNull(aggregate.id());
    }

    // --- Scenarios for Valid Command Execution ---

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    // --- Scenarios for Domain Violations ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "TS-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Force state to unauthenticated. 
        // In a real hydrated aggregate, this would be missing an 'Authenticated' event.
        // The aggregate checks 'isAuthenticated'. Default is false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // We need to simulate a last activity time that is too old.
        // We can package-private set it or rely on the aggregate constructor accepting a timestamp
        // For this implementation, let's assume we can set a fake 'now' or last activity timestamp.
        // We will implement a helper in the test or aggregate to set lastActivityTime.
        aggregate.setLastActivityTime(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "TS-NAV-ERR";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a state where the system thinks we are in a transaction, but context is invalid.
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS"); // Trying to end session while in transaction is invalid per some business rules, or implies a dirty state.
        // Per the prompt description: "Navigation state must accurately reflect...". 
        // We will treat a non-IDLE state as a violation for this scenario, or set a specific flag.
        aggregate.markNavigationStateInvalid();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // Verify it's not just UnknownCommandException (which means implementation is missing)
        assertFalse(thrownException instanceof UnknownCommandException);
        // Verify it is an IllegalStateException or IllegalArgumentException (Domain invariant violations)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}