package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in context of command execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in context of command execution
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-404");
        // Simulate unauthenticated state by starting one or some state change not handled by this story
        // But for aggregate state, we assume default is unauthenticated. 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Modeled as a command that fails if the session was started too long ago
        // Simulated here by creating a session in a stale state if needed, 
        // but usually enforced via logic inside the aggregate based on timestamps.
        aggregate = new TellerSessionAggregate("stale-session");
        // Force internal state to simulate staleness if necessary
        // aggregate.simulateStaleness(); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("nav-session");
        // Context implies specific preconditions not met
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Assume valid IDs unless specified otherwise
            // We use valid auth tokens for success case, invalid for failure
            boolean isValidAuth = !aggregate.getId().equals("session-404");
            boolean isTimedOut = aggregate.getId().equals("stale-session");
            boolean isNavInvalid = aggregate.getId().equals("nav-session");

            String authToken = isValidAuth ? "VALID_TOKEN" : null;
            
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.getId(), 
                "teller-101", 
                "terminal-A", 
                authToken
            );

            // If we are testing the timeout violation scenario, we need to tweak the aggregate state
            // or the command input to trigger it. 
            // Assuming the aggregate throws error if a session is already active/stale.
            
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
            resultEvents = List.of();
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // Check it's not a generic UnknownCommandException
        Assertions.assertFalse(capturedException.getMessage().contains("Unknown command"));
    }
}
