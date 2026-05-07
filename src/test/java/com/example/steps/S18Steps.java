package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermode.model.StartSessionCmd;
import com.example.domain.tellermode.model.SessionStartedEvent;
import com.example.domain.tellermode.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private String sessionId;
    private String tellerId;
    private String terminalId;
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Throwable caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "user-123";
        this.terminalId = "term-456";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate previous state construction if needed, though StartSession usually starts from scratch
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "terminal-T1";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, true, null, null, null);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals(sessionId, event.aggregateId());
        Assertions.assertEquals(tellerId, event.tellerId());
        Assertions.assertEquals(terminalId, event.terminalId());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "teller-002";
        this.terminalId = "terminal-T2";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // The violation is implicit in the command we will construct (authenticated = false)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "teller-003";
        this.terminalId = "terminal-T3";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // To violate this invariant using the provided command constructor (which takes a 'timeoutOverride'),
        // we would pass an invalid duration. Alternatively, we can try to start an already started session.
        // Based on the context of 'StartSession', checking for an invalid timeout configuration is the most logical mapping.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "teller-004";
        this.terminalId = "terminal-T4";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // The command accepts a 'mode'. We will pass an invalid/null mode to violate this.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Verify it's a specific type of error (e.g., IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException/IllegalArgumentException), but got: " + caughtException.getClass()
        );
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecutedForError() {
        // Reusing the same logic, but the setup (Given) will determine the validity of the input
        // We need to differentiate the command construction based on the scenario context.
        // For simplicity in this step file, we inspect the aggregate or use shared flags if complex.
        // Here we can deduce from the terminalId or state.
        
        Command cmd = null;
        if ("teller-002".equals(tellerId)) {
            // Violation: Not Authenticated
            cmd = new StartSessionCmd(sessionId, tellerId, terminalId, false, null, null, null);
        } else if ("teller-003".equals(tellerId)) {
            // Violation: Invalid Timeout (passing negative duration)
            cmd = new StartSessionCmd(sessionId, tellerId, terminalId, true, "-10m", null, null);
        } else if ("teller-004".equals(tellerId)) {
            // Violation: Invalid Mode (passing null mode)
            cmd = new StartSessionCmd(sessionId, tellerId, terminalId, true, null, null, null);
        }

        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

}
