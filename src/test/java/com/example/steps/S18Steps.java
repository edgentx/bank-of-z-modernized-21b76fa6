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
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Throwable caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // Assume valid state means authenticated, not timed out, and valid nav state
        // modeled via constructor defaults in the aggregate
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Cmd is constructed in When step, but we can store data here if needed
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // If cmd wasn't pre-conditioned by a 'Given violation' step, assume valid defaults
        if (cmd == null) {
            cmd = new StartSessionCmd("session-1", "teller-123", "terminal-456");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("SessionStartedEvent", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("teller-123", event.tellerId());
        Assertions.assertEquals("terminal-456", event.terminalId());
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail", false); // not authenticated
        this.cmd = new StartSessionCmd("session-auth-fail", "teller-123", "terminal-456");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // We simulate a 'timeout' violation by passing a flag to the aggregate constructor
        aggregate = new TellerSessionAggregate("session-timeout-fail", true, false);
        this.cmd = new StartSessionCmd("session-timeout-fail", "teller-123", "terminal-456");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-fail", true, true);
        this.cmd = new StartSessionCmd("session-nav-fail", "teller-123", "terminal-456");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Depending on implementation, could be IllegalStateException, IllegalArgumentException, or a custom DomainError
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
