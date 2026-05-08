package com.example.steps;

import com.example.domain.shared.*;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.*;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Standard setup: Authenticated, Active, Valid Navigation
        aggregate = new TellerSessionAggregate("SESSION-123");
        // Apply initialization logic programmatically to simulate an active session
        aggregate.applyInitialization(new SessionInitializedEvent("SESSION-123", "TELLER-1", Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create an unauthenticated session
        aggregate = new TellerSessionAggregate("SESSION-401");
        // No initialization event applied, leaving it unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-408");
        // Initialize with a timestamp way in the past to simulate timeout
        Instant past = Instant.now().minusSeconds(3600); // 1 hour ago
        aggregate.applyInitialization(new SessionInitializedEvent("SESSION-408", "TELLER-1", past));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-NAV-ERR");
        aggregate.applyInitialization(new SessionInitializedEvent("SESSION-NAV-ERR", "TELLER-1", Instant.now()));
        // Simulate a corrupted navigation state
        aggregate.corruptNavigationState();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The sessionId is handled by the aggregate instance variable 'aggregate'
        // No additional logic needed here unless we were loading from repo via ID string
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("SESSION-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // We expect an IllegalStateException (invariant violation) or IllegalArgumentException
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    public static class SessionInitializedEvent implements DomainEvent {
        private final String aggregateId;
        private final String tellerId;
        private final Instant occurredAt;

        public SessionInitializedEvent(String aggregateId, String tellerId, Instant occurredAt) {
            this.aggregateId = aggregateId;
            this.tellerId = tellerId;
            this.occurredAt = occurredAt;
        }
        @Override public String type() { return "session.initialized"; }
        @Override public String aggregateId() { return aggregateId; }
        @Override public Instant occurredAt() { return occurredAt; }
    }
}
