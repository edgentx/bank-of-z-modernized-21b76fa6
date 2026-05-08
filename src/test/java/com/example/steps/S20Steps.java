package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a previously authenticated, active session in IDLE state.
        // This satisfies the preconditions for a successful termination.
        aggregate.setActive("TELLER-01", true, "IDLE", Instant.now());
        this.command = new EndSessionCmd(sessionId);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the step above
        assertNotNull(command.sessionId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Create a session that was never authenticated (tellerId is null)
        String sessionId = "SESSION-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Do not set active/tellerId -> defaults to null/false
        this.command = new EndSessionCmd(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "SESSION-TIMEDOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session that is valid but last activity was 31 minutes ago
        // (Timeout is 30 mins)
        Instant ancientTime = Instant.now().minus(Duration.ofMinutes(31));
        aggregate.setActive("TELLER-01", true, "IDLE", ancientTime);
        this.command = new EndSessionCmd(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "SESSION-BUSY";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session deep in a workflow (e.g., Cash Withdrawal)
        aggregate.setActive("TELLER-01", true, "CASH_WITHDRAWAL_PENDING", Instant.now());
        this.command = new EndSessionCmd(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(command.sessionId(), event.aggregateId());
        assertNull(caughtException, "Should not throw exception on success");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Command execution should have thrown an exception");
        // The domain throws IllegalStateException for invariants
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
        
        // Verify no events were emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on rejection");
    }
}
