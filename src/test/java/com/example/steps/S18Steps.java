package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private StartSessionCmd command;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "TS-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // Pre-authenticate for the happy path
        aggregate.markAuthenticated();
        // Ensure clean state (IDLE)
        aggregate.setNavigationState("IDLE");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Logic handled in 'When' constructing command
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Logic handled in 'When' constructing command
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // If not set by specific violation steps, use valid defaults
            if (command == null) {
                command = new StartSessionCmd("TELLER-123", "TERM-01");
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "TS-AUTH-FAIL";
        aggregate = new TellerSessionAggregate(sessionId);
        // DO NOT authenticate. New aggregates are not authenticated by default.
        command = new StartSessionCmd("TELLER-999", "TERM-99");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "TS-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid auth
        aggregate.setNavigationState("IDLE"); // Ensure valid state
        aggregate.expireSession(); // Simulate timeout
        command = new StartSessionCmd("TELLER-TIMEOUT", "TERM-TO");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "TS-NAV-ERR";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid auth
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS"); // Set busy state
        command = new StartSessionCmd("TELLER-NAV", "TERM-NAV");
    }
}
