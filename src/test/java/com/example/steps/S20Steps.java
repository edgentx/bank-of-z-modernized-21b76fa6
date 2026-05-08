package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private UUID sessionId;

    // --- Given ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-123"); // Ensure auth invariant passes by default
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setUnauthenticated(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-123");
        aggregate.setStale(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-123");
        aggregate.setCriticalNavigationContext(true); // Violation
    }

    // --- When ---

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Then ---

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Expected no error, but got: " + capturedException);
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected at least one event");
        assertTrue(events.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error, but command succeeded");
        // Domain errors are typically IllegalStateException or IllegalArgumentException in this context
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Expected IllegalStateException or IllegalArgumentException, got: " + capturedException.getClass());
    }

    // --- Mock Repository for testing ---
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            return aggregate;
        }

        @Override
        public TellerSessionAggregate findById(UUID id) {
            // Not needed for this specific unit-level step logic, but required by interface
            return null;
        }
    }
}