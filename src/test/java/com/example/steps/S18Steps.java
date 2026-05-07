package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String currentTellerId = "teller-123";
    private String currentTerminalId = "term-ABC";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated(); // Assume auth is valid unless specified otherwise
        aggregate.setNavState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Intentionally not calling markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesInactivity() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.markAuthenticated();
        // Simulate a state where the session was active but timed out logic is violated or being checked incorrectly.
        // Based on our implementation, we simulate a stale activity timestamp while assuming the state allows restart logic.
        aggregate.simulateActivityPastTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.markAuthenticated();
        aggregate.setNavState("TRANSACTION_IN_PROGRESS"); // Not IDLE
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Using default valid ID
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Using default valid ID
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
