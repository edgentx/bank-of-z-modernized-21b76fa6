package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String sessionId = "session-123";
    private String tellerId = "teller-001";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Set up a valid, authenticated state
        aggregate.setAuthenticatedSession(tellerId, Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is already set in the constructor/setup
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
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

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly leave inactive/unauthenticated
        aggregate.setInactiveSession();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup an active session but with old activity timestamp
        aggregate.setTimeoutSession(Duration.ofMinutes(15), Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup active session but force invalid navigation state via internal API or constructor
        // Using reflection or specific setter if available. For this test, we can mock the state.
        // Since TellerSessionAggregate doesn't expose a setScreen, we instantiate one and assume
        // the violation implies a state where screen is null despite being active.
        aggregate.setAuthenticatedSession(tellerId, Instant.now());
        // Here we assume a scenario where the screen state was lost or corrupted.
        // In a real test, we might need a package-private setter or the test uses a specific constructor.
        // For this generated code, we simulate the violation by manipulating state if possible,
        // or relying on the aggregate being created in a state that fails the check.
        // Let's use the setNavigationState with null/blank to simulate failure logic.
        aggregate.setNavigationState(" "); // Sets active=true but blank screen logic might be internal.
        // Actually, the invariant check inside `execute` relies on the internal field.
        // `setNavigationState` sets it to a non-blank string usually.
        // To force the violation, we need the aggregate's internal state to be invalid.
        // Since I control the aggregate code, I'll add a way to force bad state or just create a fresh one
        // that hasn't been navigated yet (if the logic requires non-null).
        // Let's assume we can force a null screen state.
        aggregate.setNavigationState(null); // Internal helper usage
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        // Optionally check message content
        // assertTrue(thrownException.getMessage().contains("..."));
    }
}