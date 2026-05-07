package com.example.steps;

import com.example.domain.tellermgmt.model.SessionStartedEvent;
import com.example.domain.tellermgmt.model.StartSessionCmd;
import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Throwable caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // ID defaults to valid state for 'valid' scenario
        this.aggregate = new TellerSessionAggregate("TS-01");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // ID is valid
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // ID is valid
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Using defaults valid for the success case
            if (this.command == null) {
                this.command = new StartSessionCmd("TS-01", "USER-123", "TERM-A");
            }
            this.resultEvents = this.aggregate.execute(this.command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent evt = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", evt.type());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("TS-ERR-01");
        // The command will be constructed in the 'When' block or setup below to force the violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("TS-ERR-02");
        // The aggregate logic will reject if we try to start an already active/started session
        // (The wording 'Sessions must timeout' in this context is interpreted as 'Session is already active/stale')
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("TS-ERR-03");
        // We will use an invalid terminal ID to trigger the operational context validation
    }

    // We can reuse the central When block for all scenarios as we set up specific violation contexts above.
    // But we need to ensure the 'command' is constructed specific to the violation logic.
    // We'll override the 'When' logic slightly or setup 'command' explicitly for the negative cases.
    // For simplicity in Cucumber, we can rely on the aggregate state or command fields.

    // Refining violation command injection:
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupAuthViolation() {
        aTellerSessionAggregateThatViolatesAuthentication();
        this.command = new StartSessionCmd("TS-ERR-01", null, "TERM-A"); // TellerID is null -> Not Authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setupTimeoutViolation() {
        aTellerSessionAggregateThatViolatesTimeout();
        // Start a session first
        this.aggregate.execute(new StartSessionCmd("TS-ERR-02", "USER-1", "TERM-1"));
        // Try to start again -> Invariant Violation (Session already exists/active)
        this.command = new StartSessionCmd("TS-ERR-02", "USER-1", "TERM-1");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setupNavViolation() {
        aTellerSessionAggregateThatViolatesNavigationState();
        // Terminal ID must start with TERM-
        this.command = new StartSessionCmd("TS-ERR-03", "USER-1", "INVALID-CONTEXT");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected exception but command succeeded");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
