package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.setNavigationState("IDLE"); // Ensure valid state
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Command construction is deferred to the When step to avoid state confusion
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Command construction is deferred to the When step
    }

    // Scenario 2, 3, 4: Violations
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.markUnauthenticated(); // Violation: Not authenticated
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
        aggregate.markStale(); // Violation: Simulate staleness
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("TRANS_BUSY"); // Violation: Not IDLE
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // We assume valid IDs for the command payload itself; the aggregate checks invariants.
        command = new StartSessionCmd("teller-001", "terminal-101");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals("session-123", startedEvent.aggregateId());
        assertEquals("teller-001", startedEvent.tellerId());
        assertEquals("terminal-101", startedEvent.terminalId());
        assertNotNull(startedEvent.occurredAt());

        assertNull(caughtException, "No exception should have been thrown");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        
        // We expect IllegalStateException for domain invariant violations in this pattern
        assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException, got: " + caughtException.getClass().getSimpleName());
            
        assertTrue(caughtException.getMessage() != null && !caughtException.getMessage().isBlank(),
            "Exception message should be present");
            
        assertNull(resultEvents, "No events should be emitted on failure");
    }
}
