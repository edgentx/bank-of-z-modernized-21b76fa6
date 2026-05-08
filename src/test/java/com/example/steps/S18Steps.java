package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(true);
        aggregate.markTimedOut(false);
        aggregate.markNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateWithInvalidAuth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.markAuthenticated(false); // Violation
        aggregate.markTimedOut(false);
        aggregate.markNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatHasTimedOut() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(true);
        aggregate.markTimedOut(true); // Violation
        aggregate.markNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateWithInvalidNavState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated(true);
        aggregate.markTimedOut(false);
        aggregate.markNavigationStateValid(false); // Violation
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in context of command creation in 'When' or explicit setup
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in context of command creation in 'When' or explicit setup
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Assume context of valid IDs provided if not explicitly testing data validation failure
            // Testing Invariants here, so data validity is assumed except for Auth flag
            boolean auth = aggregate.isAuthenticated(); 
            command = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", auth);
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertNull(resultEvents); // No events committed on failure
    }
}