package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup handled in the 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup handled in the 'When' step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Valid data for the happy path
            StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-01", "terminal-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-01", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We simulate the violation by passing null/blank tellerId in the step below
        // Or we could use a specific command setup.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Use helper to manipulate state to violate invariant
        aggregate.markAsStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Use helper to manipulate state to violate invariant
        aggregate.invalidateNavigationState();
    }

    // We reuse the 'When' step, but we might need a variation for the auth failure if we can't pass nulls easily in record.
    // Java Records with null components are allowed, so the existing When step works if we override behavior.
    // However, Cucumber matches specific text. Let's rely on the specific context setup above.
    // The 'When' step uses hardcoded valid IDs. For the Auth violation, we need a specific action.

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        try {
            // Pass null tellerId to violate the check
            StartSessionCmd cmd = new StartSessionCmd("session-auth-fail", null, "terminal-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // The other violations (Timeout, NavState) fail inside execute() even with valid command data due to aggregate state.

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should have been thrown");
        // In Java DDD, domain errors are often IllegalStateException or IllegalArgumentException
        assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Exception should be a domain error (IllegalStateException/IllegalArgumentException): " + capturedException.getClass().getSimpleName()
        );
    }

}
