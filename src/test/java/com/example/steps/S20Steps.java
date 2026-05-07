package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state: Authenticated, Active, Valid Navigation
        aggregate.markAuthenticated();
        aggregate.markValidNavigationState();
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The aggregate is already initialized with ID "session-123"
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist state change in memory
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertTrue(aggregate.isEnded());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.markUnauthenticated(); // Violate auth invariant
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(); // Auth is valid
        aggregate.markTimedOut(); // Violate timeout invariant
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-nav-bad");
        aggregate.markAuthenticated(); // Auth is valid
        aggregate.markInvalidNavigationState(); // Violate navigation invariant
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        assertNull(resultEvents, "No events should be emitted on rejection");
    }

    // --- In-Memory Repository Stub ---

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
