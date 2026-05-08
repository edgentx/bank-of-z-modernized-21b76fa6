package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.ui.model.SessionStartedEvent;
import com.example.domain.ui.model.StartSessionCmd;
import com.example.domain.ui.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Data setup handled in 'When' step construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Data setup handled in 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Defaults for valid scenario
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "terminal-A", true);
        executeCommand(cmd);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // We pass 'false' for authenticated in the command to violate this
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout", Duration.ofSeconds(1));
        // Manually set the last activity to the past to simulate a stale aggregate
        aggregate.markStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Mark as active to trigger the "already active" or context mismatch error logic if implemented
        // Or simply rely on the invalid terminal ID passed in the specific command below
    }

    // Overload execution for specific error scenarios if needed, or use a contextual flag
    @When("the StartSessionCmd command is executed with invalid context")
    public void theStartSessionCmdCommandIsExecutedWithInvalidContext() {
         StartSessionCmd cmd = new StartSessionCmd("session-nav-error", "teller-1", "INVALID_CONTEXT", true);
         executeCommand(cmd);
    }

    @When("the StartSessionCmd command is executed with missing auth")
    public void theStartSessionCmdCommandIsExecutedWithMissingAuth() {
        StartSessionCmd cmd = new StartSessionCmd("session-401", "teller-1", "terminal-A", false);
        executeCommand(cmd);
    }

    @When("the StartSessionCmd command is executed on stale session")
    public void theStartSessionCmdCommandIsExecutedOnStaleSession() {
        StartSessionCmd cmd = new StartSessionCmd("session-timeout", "teller-1", "terminal-A", true);
        executeCommand(cmd);
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}