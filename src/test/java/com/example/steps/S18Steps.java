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

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Throwable thrownException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // For the command to succeed, authentication must be valid
        aggregate.markAuthenticated();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in When block
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in When block
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "terminal-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        DomainEvent event = resultEvents.iterator().next();
        assertTrue(event instanceof SessionStartedEvent);
        assertEquals("session.started", event.type());
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-456");
        // isAuthenticated defaults to false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-789");
        aggregate.markAuthenticated(); // Ensure auth is valid so we test the timeout invariant
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-101");
        aggregate.markAuthenticated();
        // We will pass an invalid terminal ID in the command to trigger this, but 
        // given the aggregate structure, let's assume the validation happens in the command execution
        // looking at aggregate state. For this test, we just need a valid aggregate, 
        // but we might mock a state that implies invalid context if the aggregate held it.
        // However, the StartSessionCmd takes the terminal ID.
        // To simulate the error, the `startSession` method needs to reject based on state.
        // Let's rely on the command execution logic.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
