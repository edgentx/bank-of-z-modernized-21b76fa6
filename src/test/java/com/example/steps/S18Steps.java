package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup for when command is created
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup for when command is created
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Default valid data for the success case
            StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "term-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("teller-1", event.tellerId());
        Assertions.assertEquals("term-A", event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    // Overriding the When step for specific context if needed, or use generic
    // We will use a specific When for negative cases to pass bad data
    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdIsExecutedWithInvalidAuth() {
        try {
            // Passing null/blank tellerId to simulate auth failure
            StartSessionCmd cmd = new StartSessionCmd("session-auth-fail", null, "term-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Force the aggregate into a state where it is active but stale
        // We need to manually mutate state to simulate a loaded stale aggregate
        aggregate.markStale(); // Helper method added to aggregate for testing
    }

    @When("the StartSessionCmd command is executed on stale session")
    public void theStartSessionCmdIsExecutedOnStaleSession() {
        try {
            // Even with valid IDs, the internal state check should fail
            StartSessionCmd cmd = new StartSessionCmd("session-timeout", "teller-1", "term-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Set navigation state to something other than IDLE to violate invariant
        aggregate.setInvalidNavigationState("ALREADY_IN_TRANSACTION");
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void theStartSessionCmdIsExecutedWithInvalidNavState() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-nav-fail", "teller-1", "term-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Depending on implementation, this could be IllegalStateException or IllegalArgumentException
        // The generic requirements ask for 'domain error', which usually maps to Exception in this layer.
    }
}
