package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // --- Scenario: Successfully execute StartSessionCmd ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // TellerId set in command construction below
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // TerminalId set in command construction below
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        command = new StartSessionCmd("session-123", "teller-01", "term-42", Instant.now());
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-42", event.terminalId());
        assertEquals("session.started", event.type());
    }

    // --- Scenario: StartSessionCmd rejected — Auth ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    // Use a specific When for this violation to differentiate
    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        // Violation: null authentication time (or expired)
        command = new StartSessionCmd("session-auth-fail", "teller-01", "term-42", null);
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(
            thrownException.getMessage().contains("authenticated") || 
            thrownException instanceof IllegalArgumentException,
            "Exception message should contain 'authenticated' or be IAE"
        );
        assertNull(resultEvents, "No events should be emitted on failure");
    }

    // --- Scenario: StartSessionCmd rejected — Timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
    }

    @When("the StartSessionCmd command is executed with stale timestamp")
    public void theStartSessionCmdCommandIsExecutedWithStaleTimestamp() {
        // Violation: Auth time is 24 hours ago (Session timeout is 12h)
        Instant staleTime = Instant.now().minus(Duration.ofHours(24));
        command = new StartSessionCmd("session-timeout-fail", "teller-01", "term-42", staleTime);
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Scenario: StartSessionCmd rejected — Navigation State ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Setup aggregate to already be active (invalid context for a 'Start' command)
        // Simulating that the context is already initialized/active elsewhere.
        StartSessionCmd init = new StartSessionCmd("session-nav-fail", "teller-01", "term-42", Instant.now());
        // Execute directly to set internal state for the test scenario
        aggregate.execute(init);
    }

    @When("the StartSessionCmd command is executed on active session")
    public void theStartSessionCmdCommandIsExecutedOnActiveSession() {
        // Attempting to start an already started session violates state transition rules
        command = new StartSessionCmd("session-nav-fail", "teller-01", "term-42", Instant.now());
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
