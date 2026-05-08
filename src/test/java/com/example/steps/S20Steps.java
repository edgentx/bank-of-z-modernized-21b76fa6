package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private SessionEndedEvent resultingEvent;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Initialize to a valid state (authenticated, active, valid nav state)
        this.aggregate.initialize("teller-456"); 
        this.repository.save(this.aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the 'Given a valid TellerSession aggregate' initialization
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // We initialize it, but force it to be unauthenticated
        this.aggregate.initialize("teller-456");
        this.aggregate.clearAuthentication();
        this.repository.save(this.aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesInactivity() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        this.aggregate.initialize("teller-456");
        // Force the session to appear timed out
        this.aggregate.markTimedOut();
        this.repository.save(this.aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        this.aggregate.initialize("teller-456");
        // Set navigation state to something that prevents ending the session
        this.aggregate.setNavigationState("IN_TRANSACTION");
        this.repository.save(this.aggregate);
    }

    // --- Whens ---

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            var cmd = new EndSessionCmd(aggregate.id());
            // Execute directly on the aggregate instance retrieved from 'repository'
            // Note: The in-memory repo returns the same instance in this simple test setup
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                this.resultingEvent = (SessionEndedEvent) events.get(0);
            }
        } catch (IllegalStateException | UnknownCommandException e) {
            this.caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultingEvent, "Expected a SessionEndedEvent to be emitted");
        assertEquals("session.ended", resultingEvent.type());
        assertEquals(aggregate.id(), resultingEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown, but none was");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof UnknownCommandException,
                "Expected IllegalStateException or UnknownCommandException, got " + caughtException.getClass().getSimpleName());
    }

    // --- In-Memory Repository Implementation for Tests ---
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final java.util.Map<String, TellerSessionAggregate> store = new java.util.HashMap<>();

        @Override
        public void save(TellerSessionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }

        @Override
        public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.ofNullable(store.get(id));
        }
    }
}
