package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception thrownException;

    // Using a simple mock repository pattern or direct instantiation for unit testing the aggregate logic
    // In a real integration test, we might inject a real repository.

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated(); // Ensure valid state
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.sessionId = "session-123";
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals("session-123", event.aggregateId());
        assertFalse(aggregate.isActive());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally do not mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.markAuthenticated(); // Valid Auth
        this.aggregate.markExpired(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = new TellerSessionAggregate("session-bad-nav");
        this.aggregate.markAuthenticated(); // Valid Auth
        // Valid Timeout (default)
        this.aggregate.markNavigationInvalid(); // Force bad nav state
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
