package com.example.steps;

import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated(); // Pre-authenticate for success case
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        if (command == null) {
            command = new StartSessionCmd("session-1", "teller-123", "term-456", Duration.ofMinutes(30), "HOME");
        }
        // If command exists, we assume it was created with valid IDs
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        if (command == null) {
            command = new StartSessionCmd("session-1", "teller-123", "term-456", Duration.ofMinutes(30), "HOME");
        }
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        assertEquals(1, events.size(), "Should have emitted exactly one event");
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Ensure NOT authenticated
        command = new StartSessionCmd("session-2", "teller-123", "term-456", Duration.ofMinutes(30), "HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout_config() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.markAuthenticated();
        // Violate invariant: Timeout must be > 0
        command = new StartSessionCmd("session-3", "teller-123", "term-456", Duration.ZERO, "HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.markAuthenticated();
        // Violate invariant: Context cannot be blank
        command = new StartSessionCmd("session-4", "teller-123", "term-456", Duration.ofMinutes(30), "");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException, 
            "Should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
