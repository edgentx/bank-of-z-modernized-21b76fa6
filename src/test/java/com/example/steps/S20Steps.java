package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String providedSessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        providedSessionId = "TS-123";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Setup standard valid state: Authenticated, active, within timeout, safe context
        aggregate.markAuthenticated("TELLER-001");
        aggregate.setNavigationContext("IDLE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The sessionId is implicitly handled by the aggregate constructor in this context,
        // but we assume the command matches.
        assertNotNull(providedSessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        providedSessionId = "TS-401";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Violation: Not authenticated
        // (default state of aggregate is not authenticated)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        providedSessionId = "TS-402";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated("TELLER-002");
        aggregate.setNavigationContext("IDLE");
        // Violation: Set last activity to 20 minutes ago (assuming 15 min timeout)
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15));
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        providedSessionId = "TS-403";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated("TELLER-003");
        // Violation: Context is CRITICAL/CASH_DRAWER_OPEN
        aggregate.setNavigationContext("CASH_DRAWER_OPEN");
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(providedSessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(providedSessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
