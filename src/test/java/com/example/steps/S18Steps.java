package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private Throwable capturedException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it meets all invariants for success
        aggregate.markAuthenticated();
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Teller ID is part of the command, constructed in the When clause
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Terminal ID is part of the command, constructed in the When clause
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // We assume valid IDs here; specific tests for invalid IDs aren't in these scenarios,
        // but we can use defaults.
        String sessionId = "session-123";
        if (aggregate == null) {
            // If the aggregate wasn't initialized by a specific Given (e.g. violation), create it here
            aggregate = new TellerSessionAggregate(sessionId);
        }

        command = new StartSessionCmd(sessionId, "teller-01", "terminal-A");
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertTrue(resultEvents.iterator().hasNext(), "At least one event expected");
        
        DomainEvent event = resultEvents.iterator().next();
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        assertEquals("session.started", event.type());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-violation-auth");
        aggregate.markUnauthenticated(); // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-violation-timeout");
        aggregate.markAuthenticated(); // Passed auth
        aggregate.expireSession(); // Violation: Expired
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        aggregate = new TellerSessionAggregate("session-violation-nav");
        aggregate.markAuthenticated(); // Passed auth
        aggregate.corruptNavigationState(); // Violation: Wrong screen/state
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}