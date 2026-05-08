package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentTellerId = "TELLER_1";
    private String currentTerminalId = "TERM_1";

    // In-memory repository implementation for testing
    private final TellerSessionRepository repository = new TellerSessionRepository() {
        // Simple mock that just returns the instance we're managing in memory for the scenario
        @Override
        public void save(TellerSessionAggregate agg) { /* No-op for this test scope */ }
        @Override
        public TellerSessionAggregate findById(String id) { return aggregate; }
        @Override
        public TellerSessionAggregate create(String id) { return new TellerSessionAggregate(id); }
    };

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = repository.create("SESSION_123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.currentTellerId = "VALID_TELLER_01";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.currentTerminalId = "TERM_XYZ";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = repository.create("SESSION_AUTH_FAIL");
        // Force the aggregate into a state where authentication is missing/invalid
        aggregate.markUnauthenticated(); 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = repository.create("SESSION_TIMEOUT");
        // Force the aggregate to appear stale
        aggregate.markStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = repository.create("SESSION_NAV_ERROR");
        // Simulate a context mismatch (e.g. terminal ID differs from expectation)
        aggregate.setNavigationContextInvalid(); 
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(currentTellerId, event.tellerId());
        assertEquals(currentTerminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception must be a domain error (IllegalStateException)");
        assertFalse(caughtException.getMessage().isBlank(), "Error message must be present");
    }
}