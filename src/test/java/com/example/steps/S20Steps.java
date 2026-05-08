package com.example.steps;

import com.example.domain.shared.Command;
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

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    // Helper to build a valid baseline aggregate
    private TellerSessionAggregate createValidAggregate() {
        // ID: "S-123", Teller: "T-001", Terminal: "TT-01", Last Active: Now
        return new TellerSessionAggregate("S-123", "T-001", "TT-01", Instant.now(), "HOME_SCREEN");
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate construction in the previous step
        assertNotNull(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            // We assume the command matches the aggregate ID for this story
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
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
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        // Unauthenticated means TellerID is null or invalid
        // "Initiate a session" maps to creating an aggregate that cannot perform actions.
        aggregate = new TellerSessionAggregate("S-FAIL-01", null, "TT-01", Instant.now(), "LOGIN_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Create a session where lastActiveTime was 30 minutes ago (simulating timeout)
        Instant pastTime = Instant.now().minus(Duration.ofMinutes(30));
        aggregate = new TellerSessionAggregate("S-TIMEOUT", "T-001", "TT-01", pastTime, "HOME_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        // Simulate a mismatch or null state which represents an invalid context for ending a session normally
        // Or a state that prevents a clean shutdown (e.g., stuck in a critical transaction)
        aggregate = new TellerSessionAggregate("S-NAV-ERR", "T-001", "TT-01", Instant.now(), null);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // The prompt specifies domain error, usually represented by IllegalStateException or IllegalArgumentException in this codebase style
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
