package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated, recent activity, HOME screen
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationState("HOME");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the aggregate construction in the previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-invalid-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark as authenticated
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationState("HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-invalid-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set last activity to 16 minutes ago (configured timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(16)));
        aggregate.setNavigationState("HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "session-invalid-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Set a navigation state that implies an active operation
        aggregate.setNavigationState("CASH_DEPOSIT_ENTRY");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should produce exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException (domain error)");
    }
}
