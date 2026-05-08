package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // This step sets up context for the command execution
        // In a real scenario, this might set the teller ID in a context holder
        // Here we rely on valid defaults in the Command execution step
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION-NO-AUTH");
        // We force the state where authenticated is false (default) but we want to test the failure
        // So we just ensure we don't authenticate.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        // We simulate a timeout by setting the last activity time far in the past
        // Exposing a package-private or test-scoped method to manipulate state for testing
        aggregate.simulateInactivity(Duration.ofMinutes(30)); // Assuming config is 15 mins
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-NAV");
        // Set navigation state to something invalid like "CRASHED" or null logic
        aggregate.simulateInvalidNavigationState("INVALID_STATE");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Using valid defaults for the command execution
            Command cmd = new StartSessionCmd(
                aggregate.id(), 
                "TELLER-123", 
                "TERM-456"
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "At least one event should be emitted");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // In Java domain, domain errors are often Exceptions (IllegalStateException, IllegalArgumentException)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected a domain rule exception, got: " + capturedException.getClass().getSimpleName()
        );
    }
}
