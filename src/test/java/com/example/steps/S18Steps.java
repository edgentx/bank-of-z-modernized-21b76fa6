package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSession("sess-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context managed in the 'When' step for this command
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context managed in the 'When' step for this command
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd("sess-123", "teller-01", "term-A", true);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("sess-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-A", event.terminalId());
        assertEquals("teller.session.started", event.type());

        // Verify Aggregate State
        assertTrue(aggregate.isActive());
        assertEquals("teller-01", aggregate.getTellerId());
        assertEquals("term-A", aggregate.getTerminalId());
    }

    // Scenario 2: Auth Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSession("sess-auth-fail");
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        StartSessionCmd cmd = new StartSessionCmd("sess-auth-fail", "teller-01", "term-A", false);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException.getMessage().contains("authenticated"));
        assertNull(resultEvents);
    }

    // Scenario 3: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSession("sess-timeout");
        // Setup state where session might be considered stale (simulated via flag or past date)
        // Since startSession creates a fresh session, we test that the logic protects against stale usage if we were loading.
        // For pure aggregate logic test, we verify the constraints.
    }

    // Scenario 4: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSession("sess-nav");
        // Force active state to test the "Already Active" invariant which prevents context reset.
        StartSessionCmd first = new StartSessionCmd("sess-nav", "teller-01", "term-A", true);
        aggregate.execute(first); // Now active
    }

    @When("the StartSessionCmd command is executed again on active session")
    public void theStartSessionCmdCommandIsExecutedAgain() {
        StartSessionCmd cmd = new StartSessionCmd("sess-nav", "teller-02", "term-B", true);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }
}
