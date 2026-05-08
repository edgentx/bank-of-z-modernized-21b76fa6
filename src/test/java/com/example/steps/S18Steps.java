package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in command construction below
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in command construction below
    }

    // Helper to construct valid command base
    private StartSessionCmd createValidCommand() {
        Set<String> roles = new HashSet<>();
        roles.add("TELLER_AUTHENTICATED");
        return new StartSessionCmd("session-123", "teller-01", "term-05", roles, "MAIN_MENU");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        if (command == null) {
            // If no specific violation setup, assume valid command was intended by context or previous steps
            // However, scenarios usually set up a specific violation state.
            // If we hit this without a command set, it's a flow error, but let's default to valid for the happy path if needed.
            command = createValidCommand();
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.sessionId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-05", event.terminalId());
    }

    // Negative Scenarios setup

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        Set<String> roles = new HashSet<>(); // No TELLER_AUTHENTICATED role
        command = new StartSessionCmd("session-123", "teller-01", "term-05", roles, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        Set<String> roles = new HashSet<>();
        roles.add("TELLER_AUTHENTICATED");
        // Using operational context to simulate the system state violation or command payload issue
        command = new StartSessionCmd("session-123", "teller-01", "term-05", roles, "TIMED_OUT");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-123");
        Set<String> roles = new HashSet<>();
        roles.add("TELLER_AUTHENTICATED");
        // Blank context violates the rule
        command = new StartSessionCmd("session-123", "teller-01", "term-05", roles, "");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check for specific exception types depending on the implementation (IllegalArgumentException or IllegalStateException)
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
