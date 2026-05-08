package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.uinav.model.SessionStartedEvent;
import com.example.domain.uinav.model.StartSessionCmd;
import com.example.domain.uinav.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Assume valid default state (not started, authenticated, valid nav)
        aggregate.markAuthenticated(); // Helper for test setup
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Scenario context setup handled in the When block via Command construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Scenario context setup handled in the When block via Command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd("session-123", "teller-1", "terminal-1");
            aggregate.execute(cmd);
        } catch (Throwable t) {
            this.thrownException = t;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-401");
        // aggregate.markAuthenticated() is NOT called, so isAuthenticated is false by default
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Force timeout state for testing
        aggregate.forceTimeout(); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-nav");
        aggregate.markAuthenticated();
        // Corrupt navigation state for testing
        aggregate.corruptNavigationState();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, "Should be a domain rule violation");
    }

}
