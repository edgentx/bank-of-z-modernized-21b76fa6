package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionStartedEvent;
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
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Set up a valid state: authenticated, correct navigation state, and recent activity
        this.aggregate.setAuthenticated(true);
        this.aggregate.setNavigationState("SIGN_ON");
        this.aggregate.setLastActivity(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // We will construct the command in the When step, this sets up the context if needed
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // We will construct the command in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Create command with valid defaults
            cmd = new StartSessionCmd("teller-1", "term-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionStartedEvent);
        TellerSessionStartedEvent event = (TellerSessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-A", event.terminalId());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-bad-auth");
        this.aggregate.setAuthenticated(false); // Violates invariant
        this.aggregate.setNavigationState("SIGN_ON");
        this.aggregate.setLastActivity(Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
        assertFalse(aggregate.isActive(), "Session should not be active after failure");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.setAuthenticated(true);
        this.aggregate.setNavigationState("SIGN_ON");
        // Set last activity to 20 minutes ago (default timeout is 15)
        this.aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-bad-nav");
        this.aggregate.setAuthenticated(true);
        this.aggregate.setNavigationState("TRANSACTION_IN_PROGRESS"); // Not SIGN_ON
        this.aggregate.setLastActivity(Instant.now());
    }

    // Additional Whens/Thens can reuse the generic definitions above
}
