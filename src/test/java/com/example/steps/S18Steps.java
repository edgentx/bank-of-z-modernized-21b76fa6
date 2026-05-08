package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private InMemoryTellerSessionRepository repository;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated(); // Ensure valid state (authenticated)
        this.repository = new InMemoryTellerSessionRepository();
        this.repository.save(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in 'When' step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-01", "term-05");
        try {
            this.resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-05", event.terminalId());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-999");
        // Do NOT markAuthenticated(). authenticated defaults to false.
        this.repository = new InMemoryTellerSessionRepository();
        this.repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("authenticated"));
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout after a configured period of inactivity.
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.markAuthenticated();
        // Simulate a session that was active a long time ago (simulating a restart attempt on timed out session)
        this.aggregate.markSessionInactiveAndTimeout(); 
        this.repository = new InMemoryTellerSessionRepository();
        this.repository.save(aggregate);
    }

    // Scenario: StartSessionCmd rejected — Navigation state must accurately reflect the current operational context.
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-nav-error");
        this.aggregate.markAuthenticated();
        // Set a state that is invalid for starting a session (e.g., mid-transaction)
        this.aggregate.setCorruptNavigationState("IN_TRANSACTION_MID_FLOW");
        this.repository = new InMemoryTellerSessionRepository();
        this.repository.save(aggregate);
    }
}
