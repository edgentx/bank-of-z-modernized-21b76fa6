package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to simulate time passage
    private Supplier<Instant> timeSupplier = Instant::now;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-123");
        // Simulate an authenticated, active session
        // We assume a constructor or a reflection helper to set the state to 'Active' and 'Authenticated'
        // For this test, we'll assume the aggregate starts in a valid initialized state,
        // or we would execute a 'StartSessionCmd' if it existed.
        // Given the constraints, we will use a package-private helper or constructor if available,
        // but standard practice is to assume the aggregate is loaded from the repo in a valid state.
        // We will mock the internal state via the specific 'test' constructor if needed, 
        // but TellerSessionAggregate below only has the standard one. 
        // To make the "Given" work for a valid session, we assume the aggregate is rehydrated.
        // I will add a helper to the Aggregate to set state for testing purposes (reflection or test-friendly method).
        
        // For simplicity in this implementation, we assume the aggregate is constructed fresh.
        // If we need to simulate an active session, we might need to execute a Start command.
        // However, since S-20 only defines EndSession, we will assume the 'Valid' aggregate
        // is constructed in a state that allows ending.
        aggregate.markAsAuthenticated();
        aggregate.markAsActive();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The ID is usually part of the Command, but the aggregate ID is used to route it.
        // Here we just ensure the aggregate instance ID matches what we expect.
        assertNotNull(aggregate.id());
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-404");
        // Intentionally do NOT mark as authenticated.
        // It is just initialized (or anonymous).
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        aggregate.markAsAuthenticated();
        aggregate.markAsActive();
        
        // Simulate time passage
        // The aggregate needs a clock supplier to handle this deterministically.
        // Assuming TellerSessionAggregate allows injecting a clock or we rely on Wall clock (slow).
        // For unit tests, we'll update the last activity timestamp to the past.
        aggregate.forceLastActivityTime(Instant.now().minus(Duration.ofMinutes(31))); // 30 min timeout configured
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-NAV-ERR");
        aggregate.markAsAuthenticated();
        // Simulate a state mismatch, e.g., aggregate thinks it's on 'Menu' but context implies 'Transaction'
        // or simply a flag that indicates inconsistency.
        aggregate.simulateNavigationStateMismatch(true);
    }

    // --- Action ---

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        // In DDD, domain errors are often IllegalStateExceptions or specific DomainExceptions.
        // The TellerSessionAggregate uses IllegalStateException.
        assertTrue(capturedException instanceof IllegalStateException, "Should be an IllegalStateException (Domain Error)");
    }

}
