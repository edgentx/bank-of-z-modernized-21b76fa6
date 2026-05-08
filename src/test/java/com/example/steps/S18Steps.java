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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    // Test State
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private StartSessionCmd cmd;

    // Constants for valid data
    private static final String VALID_SESSION_ID = "sess-123";
    private static final String VALID_TELLER_ID = "user-teller-01";
    private static final String VALID_TERMINAL_ID = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Ensure defaults are valid for the success case
        aggregate.setAuthenticated(true);
        aggregate.setOperationalContextValid(true);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Used in constructing the command later
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Used in constructing the command later
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        cmd = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(VALID_SESSION_ID, event.aggregateId());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
        assertNotNull(event.occurredAt());
        
        assertNull(thrownException, "No exception should have been thrown");
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(false); // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true); // Auth is OK
        // Simulate an old activity time to trigger timeout logic (if aggregate logic checks previous activity)
        // Note: For this specific aggregate logic, we simulate the condition that causes the rejection.
        aggregate.setLastActivityAt(Instant.now().minusSeconds(3600)); // 1 hour ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true); // Auth is OK
        aggregate.setOperationalContextValid(false); // Violation: Context invalid
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Exception must be a domain error (IllegalStateException)");
        assertFalse(thrownException instanceof UnknownCommandException, "Should not be UnknownCommandException");
    }

    // Helper for Cucumber to find the test suite runner if needed, though typically handled by separate runner class
}
