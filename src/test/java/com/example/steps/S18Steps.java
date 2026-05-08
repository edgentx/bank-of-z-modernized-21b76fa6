package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd on TellerSession.
 */
public class S18Steps {

    // Test Doubles (In-Memory)
    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    
    // Context
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    // Repository Stub
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private TellerSessionAggregate store;
        @Override public void save(TellerSessionAggregate aggregate) { this.store = aggregate; }
        @Override public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.ofNullable(store);
        }
    }

    // --------------------------------------------------------
    // Givens
    // --------------------------------------------------------

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Default valid state for happy path
        this.aggregate.setAuthenticated(true);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "TELLER_123";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "TERM_T01";
    }

    // --------------------------------------------------------
    // Violations
    // --------------------------------------------------------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(false); // Violation
        this.tellerId = "TELLER_123";
        this.terminalId = "TERM_T01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        this.tellerId = "TELLER_123";
        this.terminalId = "TERM_T01";
        
        // Set last activity to 20 minutes ago (Timeout is 15)
        this.aggregate.setLastActivityAt(Instant.now().minusSeconds(1200));
        this.aggregate.setStatus(TellerSessionAggregate.SessionStatus.ACTIVE); // Simulate it was active before
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        this.tellerId = "TELLER_123";
        this.terminalId = "TERM_T01";
        this.aggregate.setNavigationValid(false); // Violation
    }

    // --------------------------------------------------------
    // Whens
    // --------------------------------------------------------

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
        try {
            resultingEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --------------------------------------------------------
    // Thens
    // --------------------------------------------------------

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(capturedException.getMessage().contains("Invariant violated"), 
            "Exception message should indicate invariant violation: " + capturedException.getMessage());
    }
}
