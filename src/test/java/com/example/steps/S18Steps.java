package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Helper to set state for valid scenario
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateNotAuthenticated() {
        aggregate = new TellerSessionAggregate("session-401");
        // State defaults to unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateTimedOut() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markTimedOut(); // Helper to simulate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateInvalidNavState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.markNavigationInvalid(); // Helper to simulate invalid state
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup, handled in execute command via constants or context
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup, handled in execute command via constants or context
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd("session-123", "teller-1", "term-1");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("session.started", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Should have thrown exception");
        // Verify it's a domain rule violation (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
