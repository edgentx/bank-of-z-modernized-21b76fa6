package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Objects;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_42";
    private static final String SESSION_ID = "SESSION_123";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Ensure clean state for successful scenario
        aggregate.markAuthenticated(true);
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(false); // Violation: not authenticated
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(true);
        aggregate.markExpired(); // Violation: timed out
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(true);
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS"); // Violation: not IDLE
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Variables stored for use in 'When'
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Variables stored for use in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Construct command using valid constants
            command = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted, but got null.");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event.");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");

        // Verify content
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(SESSION_ID, event.aggregateId());
        Assertions.assertEquals(VALID_TELLER_ID, event.tellerId());
        Assertions.assertEquals(VALID_TERMINAL_ID, event.terminalId());

        // Verify aggregate state mutation
        Assertions.assertTrue(aggregate.isActive(), "Aggregate should be active after start");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown, but none was.");
        Assertions.assertTrue(
                thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
                "Expected a domain error (IllegalStateException/IllegalArgumentException), but got: " + thrownException.getClass().getSimpleName()
        );

        // Verify no events were emitted
        Assertions.assertTrue(
                resultEvents == null || resultEvents.isEmpty(),
                "No events should be emitted when command is rejected."
        );

        // Verify aggregate state was not mutated
        Assertions.assertFalse(aggregate.isActive(), "Aggregate should not be active after rejected command.");
    }
}
