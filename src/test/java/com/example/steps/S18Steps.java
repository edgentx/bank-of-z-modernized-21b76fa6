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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_A01";
    private static final String SESSION_ID = "SESSION_123";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Default state is inactive, unauthenticated.
        // The command execution handles the transition to active/authenticated.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Simulate a state where the teller is somehow flagged as not authenticated yet.
        // Since the aggregate starts unauthenticated, but the command starts a session,
        // we simulate a state where we CANNOT start.
        // Let's pretend the session already exists and is active, but we try to start again.
        // Or we manipulate the internal state to simulate the precondition check failing.
        // For this aggregate, 'active' implies 'authenticated' for the session lifecycle.
        // Let's set it to active to prevent re-starting, simulating a logic check failure.
        aggregate.forceSetActiveStateForTest(); // Helper would be needed or we rely on 'active' check.
        // Actually, let's just de-authenticate it if it was active.
        aggregate.deAuthenticate();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup: If it were an existing session, it would be timed out.
        // But we are starting a NEW session.
        // The error likely implies we cannot start a session that is ALREADY timed out (resumption?),
        // or the system clock is invalid. Given "StartSessionCmd", we usually create a fresh one.
        // However, to test the "violates" clause, we simulate the aggregate logic failing a timeout check.
        // Let's assume we are trying to start a session on an aggregate that represents a stale context.
        aggregate.markInactiveForTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Simulate invalid navigation context state
        aggregate.invalidateNavigationState();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Values are constant for this test scenario
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIdIsProvided() {
        // Values are constant for this test scenario
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());

        // Verify aggregate state mutation
        assertTrue(aggregate.isActive());
        assertTrue(aggregate.isAuthenticated());
        assertEquals("IDLE", aggregate.getNavigationContext());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect an IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}