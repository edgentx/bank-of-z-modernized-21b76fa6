package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermgmt.model.SessionStartedEvent;
import com.example.domain.tellermgmt.model.StartSessionCmd;
import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to reset state for each scenario
    private void resetAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.command = null;
        this.resultEvents = null;
        this.thrownException = null;
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        resetAggregate();
        // By default in our model, we need to mark authenticated for 'valid' context
        aggregate.markAsAuthenticated();
        aggregate.setNavigationState("NONE");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // We will construct the command in the 'When' step, but we can store values here if needed.
        // For this implementation, we'll define the command content directly in the When step.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Create a valid command
            this.command = new StartSessionCmd("teller-001", "terminal-A");
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        assertNotNull(resultEvents, "Result events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        resetAggregate();
        // Explicitly DO NOT mark as authenticated. Default is false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        resetAggregate();
        aggregate.markAsAuthenticated();
        // Set last activity to 31 minutes ago (configured timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(31, ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        resetAggregate();
        aggregate.markAsAuthenticated();
        // Set a state that implies we are already busy or in an invalid context for starting
        aggregate.setNavigationState("DEEP_IN_TRANSACTION_MENU");
    }

    @When("the StartSessionCmd command is executed")
    public void executeCommandForNegativeScenarios() {
        try {
            // Command content is less important for these invariants, but we provide valid data
            this.command = new StartSessionCmd("teller-001", "terminal-A");
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown, but none was.");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException, got " + thrownException.getClass().getSimpleName());
        // Ensure no events were published
        assertNull(resultEvents, "No events should be emitted when command is rejected");
    }

    // Note: The 'When' and 'Then' for negative scenarios reuse the methods above.
    // Cucumber matches method names, so we don't need duplicate methods unless logic differs significantly.
}
