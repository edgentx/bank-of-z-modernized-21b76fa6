package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 * Uses the InMemoryTellerSessionRepository to verify domain logic.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    private String tellerId;
    private String terminalId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        this.aggregate = repository.getOrCreate(sessionId);
        this.capturedException = null;
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "teller-001";
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "terminal-T4";
    }

    // ----------------------------------------------------------------
    // Scenarios: Rejected
    // ----------------------------------------------------------------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        // Create aggregate
        this.aggregate = repository.getOrCreate("session-violate-auth");
        // Setup: To simulate auth violation in this context, we ensure the command
        // would fail (e.g. by providing a null/blank tellerId in the When step or
        // setting internal state if the aggregate tracks it externally).
        // The Aggregate logic requires a non-null tellerId. We simulate the violation
        // by passing a bad ID later, or here we can set the ID to null.
        this.tellerId = null; 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = repository.getOrCreate("session-violate-timeout");
        // Use the helper method to corrupt the invariant
        this.aggregate.violateTimeoutConfig();
        // Setup valid defaults for other fields so we don't hit other errors first
        this.tellerId = "teller-001";
        this.terminalId = "terminal-T4";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = repository.getOrCreate("session-violate-nav");
        // Corrupt the state
        this.aggregate.violateNavigationState();
        // Setup valid defaults
        this.tellerId = "teller-001";
        this.terminalId = "terminal-T4";
    }

    // ----------------------------------------------------------------
    // Actions
    // ----------------------------------------------------------------

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
        try {
            this.resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    // ----------------------------------------------------------------
    // Outcomes
    // ----------------------------------------------------------------

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
