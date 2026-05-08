package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20: TellerSession EndSessionCmd.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a valid session: authenticated, active, recent activity, valid context
        this.aggregate = new TellerSessionAggregate(
            "session-123",
            true,                       // authenticated
            true,                       // active
            Instant.now(),              // lastActivityAt (recent)
            "HOME"                      // currentContext
        );
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate construction in the previous step or implicitly in the command
        // The command is created in the @When step.
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd("session-123");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        // Verify aggregate state change
        assertFalse(aggregate.isActive(), "Session should be inactive");
        assertFalse(aggregate.isAuthenticated(), "Session should be cleared");
        assertNull(aggregate.getCurrentContext(), "Context should be cleared");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Valid in all ways except authentication
        this.aggregate = new TellerSessionAggregate(
            "session-bad-auth",
            false,                      // NOT authenticated
            true,
            Instant.now(),
            "HOME"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Valid in all ways except activity time (old)
        this.aggregate = new TellerSessionAggregate(
            "session-timeout",
            true,
            true,
            Instant.now().minus(Duration.ofHours(1)), // Activity 1 hour ago (timeout is 15m)
            "HOME"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Valid in all ways except context state
        // Using a magic string 'PENDING_TRANSACTION' which is rejected by the aggregate logic
        this.aggregate = new TellerSessionAggregate(
            "session-bad-context",
            true,
            true,
            Instant.now(),
            "PENDING_TRANSACTION" // Invalid context for ending session
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
