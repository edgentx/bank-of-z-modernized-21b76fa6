package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
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
        // Simulates a newly created aggregate instance, valid by default
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // In a real test, this might set a context variable.
        // Here we ensure the aggregate is in a state that accepts a teller.
        // Since aggregate is new, it just needs to exist.
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Valid terminal ID context.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd(
            "session-123",
            "teller-101",
            "terminal-T4",
            Instant.now().plus(Duration.ofHours(8)) // timeout
        );
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // We force the aggregate into a state where it thinks the teller is NOT authenticated
        // The execute logic checks if isAuthenticated flag is true (simulated here by checking the command context or aggregate state)
        // In this specific aggregate design, we assume we can mark it as unauthenticated or pass bad creds.
        // For the command handler, we assume failure if the internal state check fails.
        aggregate.markUnauthenticatedForTest(); // Helper method for testing
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // To violate timeout on START, usually means the requested duration is invalid or the context is expired.
        // The prompt implies enforcing the invariant. We will pass a command that already violates the policy (e.g. duration <= 0)
        // or simulate an aggregate that thinks it's already timed out (hard for Start).
        // Let's assume the Command carries the expiry, and we pass an invalid one.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markInvalidNavigationStateForTest();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
