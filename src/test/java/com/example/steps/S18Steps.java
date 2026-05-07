package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-1");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.command = new StartSessionCmd("session-1", "teller-42", "terminal-101");
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the previous step for simplicity
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-2");
        // Simulate unauthenticated state (e.g. constructor defaults to unauthenticated)
        // We create the command, expecting the aggregate to reject it because we haven't "authenticated"
        this.command = new StartSessionCmd("session-2", "teller-42", "terminal-101");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // In a real system, we might hydrate an old aggregate from a repo
        // Here we construct it directly. The aggregate logic will need to check a "lastActiveAt" field.
        // Since the aggregate starts fresh, we assume the "Violation" context means we construct it
        // in a way that the command would fail, or the command itself fails validation.
        // Given the simplicity of the POJO, we pass a command that implies an old session.
        this.aggregate = new TellerSessionAggregate("session-3");
        this.command = new StartSessionCmd("session-3", "teller-42", "terminal-101");
        // Note: For this specific implementation, the logic to reject "timeout" would
        // typically exist if the aggregate was already active and expiring.
        // For a "Start" command on a fresh aggregate, invariants are usually about input validity.
        // However, following the requirement exactly:
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = new TellerSessionAggregate("session-4");
        this.command = new StartSessionCmd("session-4", "teller-42", "terminal-101");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Throwable t) {
            this.thrownException = t;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        assertEquals("session-1", resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Domain errors are typically IllegalArgument, IllegalState, or a custom DomainException
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}