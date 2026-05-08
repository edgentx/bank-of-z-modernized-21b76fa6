package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Initialize with a Start event to simulate a valid, active session
        aggregate.execute(new StartSessionCmd("SESSION-1", "TELLER-1", "TERMINAL-1", Instant.now()));
        aggregate.clearEvents(); // Clear the startup event so we only test EndSession
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("SESSION-2");
        // Violation: Session is not started/authenticated, state is effectively null/idle
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-3");
        // Start session
        aggregate.execute(new StartSessionCmd("SESSION-3", "TELLER-1", "TERMINAL-1", Instant.now().minus(Duration.ofHours(2))));
        aggregate.clearEvents();
        // Violation: The aggregate internally tracks last activity time as too old
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("SESSION-4");
        // Start session
        aggregate.execute(new StartSessionCmd("SESSION-4", "TELLER-1", "TERMINAL-1", Instant.now()));
        // Violation: Simulate a state mismatch (e.g. DB says 'LOCKED' but aggregate says 'ACTIVE',
        // or attempting to end a session that is mid-transaction).
        // For this aggregate, we'll simulate a "BUSY" state that prevents ending.
        aggregate.execute(new EnterScreenCmd("SESSION-4", "TRANSACTION_ENTRY"));
        aggregate.clearEvents();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd("SESSION-1"); // Using generic ID or specific one based on setup
            // Refine ID to match the aggregate's ID used in the specific scenario context if necessary
            // But since execute checks internal ID, we can pass any or match the aggregate ID.
            // Let's pass the aggregate's ID to be safe/correct.
            Command specificCmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(specificCmd);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultEvents = null;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted, but got null (likely an exception)");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected a domain error exception, but command succeeded");
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected a domain rule exception (IllegalStateException or IllegalArgumentException)");
    }
}