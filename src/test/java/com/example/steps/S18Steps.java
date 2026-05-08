package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute StartSessionCmd

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in command construction below
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in command construction below
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        executeStandardCommand();
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated to initiate a session.

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        command = new StartSessionCmd(
            "session-auth-fail",
            "teller-1",
            "terminal-1",
            false, // Not authenticated
            new NavigationState("SIGNON", "Enter"),
            Instant.now()
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Specific checks depending on exception type (IllegalStateException vs IllegalArgumentException)
        // In StartSessionCmd logic, auth failure throws IllegalArgumentException
        assertTrue(caughtException instanceof IllegalArgumentException);
        assertTrue(caughtException.getMessage().contains("Authentication required"));
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout after a configured period of inactivity.

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate high inactivity
        Instant past = Instant.now().minusSeconds(3600); // 1 hour ago
        command = new StartSessionCmd(
            "session-timeout",
            "teller-2",
            "terminal-2",
            true,
            new NavigationState("TIMEOUT", "Clear"),
            past
        );
    }

    // Scenario: StartSessionCmd rejected — Navigation state must accurately reflect the current operational context.

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Invalid Navigation State
        command = new StartSessionCmd(
            "session-nav-fail",
            "teller-3",
            "terminal-3",
            true,
            null, // Invalid state
            Instant.now()
        );
    }

    // Helper methods

    private void executeStandardCommand() {
        if (command == null) {
            command = new StartSessionCmd(
                "session-123",
                "teller-1",
                "terminal-1",
                true,
                new NavigationState("MAIN_MENU", "PF03"),
                Instant.now()
            );
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
