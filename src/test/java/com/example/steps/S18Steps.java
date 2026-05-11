package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Preconditions for success
        aggregate.markAuthenticated(); // Authenticated
        aggregate.setNavigationState("HOME"); // Valid context
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // TellerId is provided in the When step, usually
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // TerminalId is provided in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command data if not otherwise specified in scenario flow
        if (cmd == null) {
            cmd = new StartSessionCmd("session-123", "teller-42", "terminal-01");
        }
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        assertNull(caughtException, "No exception should have occurred");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // isAuthenticated defaults to false
        aggregate.setNavigationState("HOME");
        cmd = new StartSessionCmd("session-123", "teller-42", "terminal-01");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.markStale(); // Set last activity to > 15 mins ago
        aggregate.setNavigationState("HOME");
        cmd = new StartSessionCmd("session-123", "teller-42", "terminal-01");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("TRANSACTION_MENU"); // Invalid for Start
        cmd = new StartSessionCmd("session-123", "teller-42", "terminal-01");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException, "Must be a domain error (IllegalStateException or IllegalArgumentException)");
        assertNull(resultingEvents || resultingEvents.isEmpty(), "No events should be emitted on failure");
    }
}
