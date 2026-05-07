package com.example.steps;

import com.example.domain.shared.Command;
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
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true); // Pre-condition for validity
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Command construction is deferred to When, usually
        // or we set a default
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Command construction deferred
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Assume defaults if not specified by scenario
            String tid = "teller-1";
            String term = "term-01";
            cmd = new StartSessionCmd("session-123", tid, term);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-999");
        aggregate.setAuthenticated(false); // The violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        // Set activity to way in the past
        aggregate.setLastActivityAt(Instant.now().minusSeconds(3600)); // 1 hour ago
        // Note: The logic to actually *reject* based on this timestamp would be inside the aggregate
        // execute method. We add a check there to verify the state.
        // (See TellerSessionAggregate modification for this invariant)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.setAuthenticated(true);
        // This violation is context-dependent, but we can simulate it by preparing a command with bad data
        // or setting the aggregate state such that it knows it can't proceed.
        // For this BDD, we assume the command context is the driver.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecutedForRejection() {
        try {
            String tid = "teller-1";
            String term = "term-01";
            cmd = new StartSessionCmd(aggregate.id(), tid, term);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception but command succeeded");
        // Check for specific exception types if needed (IllegalStateException, IllegalArgumentException)
    }
}
