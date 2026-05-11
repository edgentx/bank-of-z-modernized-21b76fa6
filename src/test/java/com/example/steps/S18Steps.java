package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception capturedException;
    private StartSessionCmd currentCmd;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        assertNull(capturedException);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in setup for execution
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in setup for execution
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Default command construction for positive path
            if (currentCmd == null) {
                currentCmd = new StartSessionCmd(
                    "session-123",
                    "teller-001",
                    "terminal-05",
                    true, // authenticated
                    "MAIN_MENU",
                    Instant.now()
                );
            }
            resultEvents = aggregate.execute(currentCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-05", event.terminalId());
    }

    // Scenario 2: Authentication Violation
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-999");
        // Create command where authenticated is FALSE
        currentCmd = new StartSessionCmd(
            "session-999",
            "teller-unauth",
            "terminal-00",
            false, // Violation: not authenticated
            "MAIN_MENU",
            Instant.now()
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario 3: Timeout Violation
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate an invalid timestamp (e.g. null) to trigger validation logic or failure
        currentCmd = new StartSessionCmd(
            "session-timeout",
            "teller-001",
            "terminal-05",
            true,
            "MAIN_MENU",
            null // Violation: Null timestamp causing failure
        );
    }

    // Scenario 4: Navigation State Violation
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-bad");
        // Context is blank/null
        currentCmd = new StartSessionCmd(
            "session-nav-bad",
            "teller-001",
            "terminal-05",
            true,
            "", // Violation: Invalid context
            Instant.now()
        );
    }
}