package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    // Constructor to initialize the aggregate with a valid ID
    public S18Steps() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Aggregate created in constructor is valid (no state violations)
        this.aggregate = new TellerSessionAggregate("session-123");
        this.thrownException = null;
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Valid ID context handled in the execution step
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Valid ID context handled in the execution step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Assume a flag is set internally to mark auth failure, or we mock the check.
        // We force the aggregate into a state where isAuthenticated returns false.
        // Since constructor defaults to false, we just ensure it's not authenticated.
        this.aggregate = new TellerSessionAggregate("session-401");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Force the last activity time to be way in the past
        this.aggregate = new TellerSessionAggregate("session-408");
        this.aggregate.forceLastActivityTime(java.time.Instant.now().minusSeconds(3600)); // 1 hour ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // Set a state that represents a locked or invalid navigation context
        this.aggregate = new TellerSessionAggregate("session-500");
        this.aggregate.setNavigationState("LOCKED");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd("teller-1", "terminal-1");
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "One event should be emitted");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        Assertions.assertNull(thrownException, "No exception should have been thrown");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected a domain error exception");
        // In Java Domain, we typically use IllegalStateException or IllegalArgumentException for invariant violations
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Exception should be a domain error (IllegalStateException/IllegalArgumentException)"
        );
        Assertions.assertNull(resultEvents, "No events should be emitted on rejection");
    }
}
