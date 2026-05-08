package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "ts-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Setup a valid, active state
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        sessionId = "ts-456";
        aggregate = new TellerSessionAggregate(sessionId);
        // Deliberately do not call markAuthenticated, leaving it unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "ts-789";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markTimedOut(); // Sets lastActivity to past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        sessionId = "ts-101";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markInvalidNavigation(); // Sets state to INVALID
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by aggregate constructor in Given steps
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("session.ended", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
