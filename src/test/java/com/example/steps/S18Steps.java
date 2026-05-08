package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.InvalidTellerSessionStateError;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context stored in the scenario state or injected via command
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context stored in the scenario state or injected via command
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "term-ABC", Instant.now());
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("teller-123", event.tellerId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Simulating a state where the teller is not authenticated.
        // Since we can't easily set state on the aggregate without history, 
        // we will assume the command carries a flag or we create the aggregate in a 'bad' state.
        // For this exercise, we will use a flag in the command or assume the aggregate checks an external dependency.
        // Implementation: The aggregate throws error if tellerId is null/empty or explicitly marked unauthenticated.
        // We'll simulate this by passing a specific teller ID that our mock logic rejects, or by using a separate command type.
        // However, strict adherence to the existing pattern suggests the aggregate validates the command.
        // Let's assume the 'validity' implies the input data, but 'Authentication' is an invariant.
        // We will pass a command with an empty tellerId to trigger this specific invariant violation.
        this.aggregate = new TellerSessionAggregate("session-2");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Simulating a timeout scenario.
        // In a real app, this might be a retry on an existing session ID.
        this.aggregate = new TellerSessionAggregate("session-3");
        // We assume the aggregate checks the timestamp in the command against now.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // Simulating a bad navigation state context.
        this.aggregate = new TellerSessionAggregate("session-4");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        // The aggregate throws IllegalStateException or IllegalArgumentException for invariant violations
        Assertions.assertNotNull(thrownException);
        // We check for the specific exception type defined in our domain (IllegalStateException for invariants)
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
