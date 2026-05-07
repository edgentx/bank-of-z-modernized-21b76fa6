package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: Teller Session Start Feature.
 */
public class S18Steps {

    // Test Context
    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Constants
    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_TELLER_ID = "teller-001";
    private static final String VALID_TERMINAL_ID = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Default valid state: Authenticated, IDLE, Valid Context
        aggregate.markAuthenticated(true);
        aggregate.setState(TellerSessionAggregate.SessionState.IDLE);
        aggregate.setNavigationContext("HOME");
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in @When block construction
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in @When block construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            command = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(VALID_TELLER_ID, startedEvent.tellerId());
        assertEquals(VALID_TERMINAL_ID, startedEvent.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(false); // Violation
        aggregate.setState(TellerSessionAggregate.SessionState.IDLE);
        aggregate.setNavigationContext("HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(true);
        aggregate.setState(TellerSessionAggregate.SessionState.TIMED_OUT); // Violation
        aggregate.setNavigationContext("HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(true);
        aggregate.setState(TellerSessionAggregate.SessionState.ERROR); // Violation context mismatch
        aggregate.setNavigationContext("INVALID_CONTEXT");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
        assertFalse(caughtException.getMessage().isBlank(), "Exception message should not be blank");
    }
}
