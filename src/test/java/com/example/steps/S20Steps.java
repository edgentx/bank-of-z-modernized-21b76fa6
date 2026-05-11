package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermgmt.model.*;
import com.example.domain.tellermgmt.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    // In-Memory Infrastructure
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // State tracking for building the aggregate
    private boolean isAuthenticated = true;
    private boolean isActive = true;
    private Instant lastActivityAt = Instant.now();
    private boolean isNavigationValid = true;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "ts-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate an active, authenticated session via internal state setter (for testing purposes)
        // In real persistence, this would be loaded from events.
        aggregate.setTestState("teller-01", true, true, Instant.now(), true);
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicit in the aggregate creation above, retrieved here for clarity
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "ts-no-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Teller is NOT authenticated
        aggregate.setTestState("teller-01", false, true, Instant.now(), true);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "ts-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Last activity was 30 minutes ago (assuming timeout is 15)
        Instant oldActivity = Instant.now().minus(30, ChronoUnit.MINUTES);
        aggregate.setTestState("teller-01", true, true, oldActivity, true);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "s-nav-bad";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Navigation state is invalid/dirty
        aggregate.setTestState("teller-01", true, true, Instant.now(), false);
        repository.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id(), Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertEquals("teller.session.ended", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We check for specific IllegalStateException or IllegalArgumentException messages
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException,
                   "Expected a domain rule exception");
    }
}