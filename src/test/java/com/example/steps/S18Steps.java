package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure authenticated state for success path
        aggregate.markAuthenticated();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "terminal-05";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals("session.started", event.type());
        assertTrue(aggregate.isStarted());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally do NOT markAuthenticated
        assertFalse(aggregate.isStarted()); // Check initial state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Auth is fine
        aggregate.markTimedOut(); // But it is timed out
        // Simulate a command execution setup
        this.tellerId = "teller-01";
        this.terminalId = "terminal-05";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Auth is fine
        aggregate.markActive(); // It is already active (state conflict)
        // Simulate a command execution setup
        this.tellerId = "teller-01";
        this.terminalId = "terminal-05";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected exception to be thrown");
        // We check for IllegalStateException which represents a Domain Rule Violation
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage() != null && !thrownException.getMessage().isBlank());
    }
}