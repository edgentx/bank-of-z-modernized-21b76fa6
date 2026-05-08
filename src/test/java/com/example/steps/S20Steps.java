package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup a valid, authenticated, active session
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate initialization (state setup) as if created via a StartSessionCmd
        aggregate.markAuthenticated("teller-456");
        aggregate.updateLastActivity(Instant.now());
        aggregate.setOperationalContext("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate ID in 'a_valid_teller_session_aggregate'
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-invalid-auth");
        // Intentionally do NOT mark authenticated.
        // Ensure it's not timed out and context is valid so we isolate the auth failure.
        aggregate.updateLastActivity(Instant.now());
        aggregate.setOperationalContext("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Mark authenticated and valid context
        aggregate.markAuthenticated("teller-456");
        aggregate.setOperationalContext("IDLE");
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        // Mark authenticated and active
        aggregate.markAuthenticated("teller-456");
        aggregate.updateLastActivity(Instant.now());
        // Intentionally set a null/invalid context or one that indicates inconsistency
        aggregate.setOperationalContext(null); // Simulating state mismatch
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In this domain implementation, invariants are enforced by IllegalStateException
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(capturedException.getMessage().contains("Cannot end session"),
                "Exception message should contain context about rejection");
    }
}
