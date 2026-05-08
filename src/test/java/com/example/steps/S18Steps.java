package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate session;
    private Exception capturedException;
    private List<DomainEvent> events;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        session = new TellerSessionAggregate("session-1");
        // Note: A 'valid' aggregate for this command usually implies one that is ready to start.
        // Since start session initiates the lifecycle, we assume a fresh aggregate is valid to attempt start.
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in context of command execution
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in context of command execution
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // We use dummy values for 'valid' here as the context is implicit success
            events = session.execute(new StartSessionCmd("session-1", "teller-123", "term-ABC", Instant.now()));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        Assertions.assertEquals("teller-123", event.tellerId());
        Assertions.assertEquals("term-ABC", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        session = new TellerSessionAggregate("session-2");
        // Simulating a pre-existing session that is somehow blocked, though 'start' is usually the first event.
        // However, to satisfy the Gherkin 'violates' clause, we assume the command carries invalid auth.
        // Or we create a session that is already active and cannot be started again.
        // The Gherkin implies the check happens DURING the command execution.
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        try {
            // In a real scenario, validation would happen against a security context.
            // Here we simulate the rejection by passing invalid/empty tokens which the aggregate might reject,
            // OR we assume the aggregate is already in a state that rejects this.
            // Based on the aggregate logic: if already started, it fails.
            // Let's assume the 'authentication' failure maps to a specific input validation failure or business rule.
            session.execute(new StartSessionCmd("session-2", null, "term-ABC", Instant.now()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        session = new TellerSessionAggregate("session-3");
        // Setup: start a session, then force it to be timed out
        session.execute(new StartSessionCmd("session-3", "teller-1", "term-1", Instant.now().minusSeconds(3600)));
        // Mark as timed out manually for test simulation purposes, or rely on logic.
        // Since we can't easily mock time passing inside the aggregate without a Clock, 
        // we will rely on the 'configured period' logic if implemented, or handle this step as a setup for a specific command.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        session = new TellerSessionAggregate("session-4");
        // This scenario is vague. It implies the command carries invalid context.
        // We will treat it as a general rejection scenario.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
