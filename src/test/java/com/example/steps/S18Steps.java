package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Scenario: Successfully execute StartSessionCmd ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in execution step
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in execution step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-01", "terminal-A");
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
    }

    // --- Scenario: StartSessionCmd rejected — Auth required ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-999");
        // The violation will be simulated by passing null/empty tellerId in the command execution step
    }

    // We reuse the When step above, but need a specific override for the violation scenario context
    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        StartSessionCmd cmd = new StartSessionCmd("session-999", "", "terminal-B"); // Empty tellerId
        executeCommand(cmd);
    }

    // --- Scenario: StartSessionCmd rejected — Sessions must timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate that the session was created a long time ago (violating timeout)
        long pastTime = Instant.now().minus(Duration.ofHours(1)).toEpochMilli();
        aggregate.setLastActivityAt(Instant.ofEpochMilli(pastTime));
    }

    // --- Scenario: StartSessionCmd rejected — Navigation state ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav");
        // Simulate already active session (invalid state transition)
        aggregate.setState(TellerSessionAggregate.SessionState.ACTIVE);
    }

    // --- Generic Rejection Steps ---

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error (exception) but command succeeded");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    // Helper
    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
