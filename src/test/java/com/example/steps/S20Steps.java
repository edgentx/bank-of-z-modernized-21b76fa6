package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "TS-12345";
        this.aggregate = new TellerSessionAggregate(sessionId, "TELLER-99");
        // Simulate the session passing the "authenticated" check implicitly for the 'valid' scenario,
        // but we must explicitly authenticate it to pass business logic checks in the aggregate.
        aggregate.markAuthenticated();
        aggregate.markNavigationContext("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Already setup in the previous step, reiterating intent.
        assertNotNull(sessionId);
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "TS-VIOLATE-AUTH";
        this.aggregate = new TellerSessionAggregate(sessionId, "TELLER-UNAUTH");
        // Intentionally do NOT mark authenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "TS-VIOLATE-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId, "TELLER-TIMEOUT");
        aggregate.markAuthenticated();
        // Force the session to look expired
        aggregate.expireSession();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "TS-VIOLATE-NAV";
        this.aggregate = new TellerSessionAggregate(sessionId, "TELLER-NAV");
        aggregate.markAuthenticated();
        // Corrupt the navigation state
        aggregate.corruptNavigationState();
    }

    // --- Actions ---

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(this.sessionId);
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null list.");
        assertEquals(1, resultEvents.size(), "Expected exactly one event.");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent.");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected command to throw an exception, but it did not.");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException, but got " + caughtException.getClass().getSimpleName());
    }
}