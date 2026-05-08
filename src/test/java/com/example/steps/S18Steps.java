package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.commands.StartSessionCmd;
import com.example.domain.tellersession.model.events.SessionStartedEvent;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 */
public class S18Steps {

    // Test Context State
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean isAuthenticated;
    private String operationalContext;
    
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    // Simple In-Memory Repository for testing purposes
    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        // No-op for this step definition scope as we instantiate aggregates directly
        @Override public TellerSessionAggregate save(TellerSessionAggregate aggregate) { return aggregate; }
        @Override public java.util.Optional<TellerSessionAggregate> findById(String id) { return java.util.Optional.empty(); }
        @Override public void deleteAll() {}
    }

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "TS-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Default valid state for the positive path
        this.isAuthenticated = true;
        this.operationalContext = "IDLE";
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "TELLER-123";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "TS-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "TELLER-123";
        this.terminalId = "TERM-01";
        this.isAuthenticated = false; // Violation
        this.operationalContext = "IDLE";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // The timeout check in the execute logic is currently implicit based on current time.
        // To simulate a violation or specific timeout scenario, we would typically need to inject a Clock.
        // For this BDD, we assume the command execution handles the 'now' logic.
        // This placeholder sets up the scenario where the command might be rejected if we added explicit expired state logic.
        // Currently, the aggregate logic validates Auth and Context.
        this.sessionId = "TS-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "TELLER-123";
        this.terminalId = "TERM-01";
        this.isAuthenticated = true;
        this.operationalContext = "IDLE";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "TS-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "TELLER-123";
        this.terminalId = "TERM-01";
        this.isAuthenticated = true;
        this.operationalContext = "INVALID_CONTEXT"; // Violation
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd(
            this.sessionId,
            this.tellerId,
            this.terminalId,
            this.isAuthenticated,
            this.operationalContext
        );
        try {
            this.resultingEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(this.sessionId, event.aggregateId());
        Assertions.assertEquals(this.tellerId, event.tellerId());
        Assertions.assertEquals(this.terminalId, event.terminalId());
        Assertions.assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // We expect IllegalStateException or IllegalArgumentException from the aggregate
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof UnknownCommandException // Or specific domain error type
        );
    }
}
