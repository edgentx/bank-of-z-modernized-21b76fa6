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
    private String sessionToken;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to construct a valid base command
    private StartSessionCmd.Builder baseCmd() {
        return new StartSessionCmd.Builder(
            "session-123",
            "teller-01",
            "term-3270-01",
            true, // authenticated
            true, // terminal available
            900000L // 15 min timeout
        );
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateViolatingAuth() {
        aggregate = new TellerSessionAggregate("session-invalid-auth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateViolatingTimeoutConfig() {
        aggregate = new TellerSessionAggregate("session-invalid-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateViolatingNavigationState() {
        aggregate = new TellerSessionAggregate("session-invalid-nav");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        sessionToken = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context implies we just need to ensure the command constructed uses this.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd;

        // Determine which violation scenario we are in based on the aggregate ID or state
        if (aggregate.id().equals("session-invalid-auth")) {
            cmd = baseCmd().withAuthenticated(false).build();
        } else if (aggregate.id().equals("session-invalid-timeout")) {
            cmd = baseCmd().withTimeout(0L).build(); // 0 or negative time violates the rule
        } else if (aggregate.id().equals("session-invalid-nav")) {
            cmd = baseCmd().withTerminalAvailable(false).build();
        } else {
            // Standard success case
            cmd = baseCmd().build();
        }

        try {
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-3270-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // --- Builder Helper ---
    // Included in Steps file for simplicity of serialization, typically in separate model file
    public static class Builder {
        String sessionId;
        String tellerId;
        String terminalId;
        boolean authenticated;
        boolean terminalAvailable;
        long timeoutMs;

        public Builder(String sessionId, String tellerId, String terminalId, boolean authenticated, boolean terminalAvailable, long timeoutMs) {
            this.sessionId = sessionId;
            this.tellerId = tellerId;
            this.terminalId = terminalId;
            this.authenticated = authenticated;
            this.terminalAvailable = terminalAvailable;
            this.timeoutMs = timeoutMs;
        }
        public StartSessionCmd withAuthenticated(boolean val) { this.authenticated = val; return new StartSessionCmd(sessionId, tellerId, terminalId, val, terminalAvailable, timeoutMs); }
        public StartSessionCmd withTerminalAvailable(boolean val) { this.terminalAvailable = val; return new StartSessionCmd(sessionId, tellerId, terminalId, authenticated, val, timeoutMs); }
        public StartSessionCmd withTimeout(long val) { this.timeoutMs = val; return new StartSessionCmd(sessionId, tellerId, terminalId, authenticated, terminalAvailable, val); }
        public StartSessionCmd build() { return new StartSessionCmd(sessionId, tellerId, terminalId, authenticated, terminalAvailable, timeoutMs); }
    }
}
