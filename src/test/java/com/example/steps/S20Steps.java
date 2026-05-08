package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.model.TellerSessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSession session;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "session-123";
        this.session = new TellerSession(sessionId);
        // Simulate an active, authenticated session
        session.markAuthenticated("teller-01");
        session.setCurrentScreen("HOME"); // Valid operational context
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "session-unauth";
        this.session = new TellerSession(sessionId);
        // Do NOT mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "session-timeout";
        this.session = new TellerSession(sessionId);
        session.markAuthenticated("teller-01");
        // Set timeout to the past to simulate expiration
        session.setSessionTimeoutAt(Instant.now().minusSeconds(60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "session-locked";
        this.session = new TellerSession(sessionId);
        session.markAuthenticated("teller-01");
        // Set screen to a state that implies inability to end session normally
        session.setCurrentScreen("LOCKED");
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            this.resultEvents = session.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);
        
        TellerSessionEndedEvent event = (TellerSessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertFalse(session.isActive());
        assertFalse(session.isAuthenticated());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        
        // Ensure no events were emitted on failure
        assertTrue(resultEvents == null || resultEvents.isEmpty());
    }
}
