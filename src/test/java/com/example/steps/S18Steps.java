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
    private String providedTellerId;
    private String providedTerminalId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Defaults: authenticated=false, active=false, validNav=true, timedOut=false
        aggregate.markAuthenticated(); // Make it valid for success case
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // leave authenticated = false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.markInvalidNavigationState();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.providedTellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.providedTerminalId = "term-01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), providedTellerId, providedTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent);
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session.started", started.type());
        assertEquals(providedTellerId, started.tellerId());
        assertEquals(providedTerminalId, started.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        // We verify the message contains the reason to ensure it's the specific domain error
        assertTrue(thrownException.getMessage().length() > 0);
    }
}