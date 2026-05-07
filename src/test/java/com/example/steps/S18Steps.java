package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Parameters are handled in the When step via Command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Parameters are handled in the When step via Command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Default valid data for success scenario
            StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "terminal-1");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We simulate the violation by passing an authenticated=false flag in the command context
        // or relying on the aggregate to check a precondition. In this BDD context, we'll assume
        // the command carries the state, or the aggregate is in a state where it can't accept.
        // For this test, we will set the command's authenticated flag to false in the When step.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // In a real system, this might involve replaying events to set a last-active timestamp.
        // Here, we will pass a command indicating a stale context.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // We'll pass a command with invalid state details in the When step.
    }

    @When("the StartSessionCmd command is executed on invalid context")
    public void the_start_session_cmd_command_is_executed_with_violation() {
        try {
            StartSessionCmd cmd;
            
            // Heuristic to determine which violation scenario we are in based on ID prefix
            if (aggregate.id().contains("auth")) {
                // Not authenticated
                cmd = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1", false, true, true);
            } else if (aggregate.id().contains("timeout")) {
                // Timed out (Stale)
                cmd = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1", true, false, true);
            } else {
                // Bad navigation state
                cmd = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1", true, true, false);
            }
            
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        Assertions.assertNull(resultEvents); // No events should be emitted on failure
    }

    // Wiring for Cucumber to find the default 'When' step
    @When("the StartSessionCmd command is executed")
    public void execute_start_session_cmd_default() {
        // Delegates to the positive execution or checks context. 
        // Cucumber will match the first defined step, but since we have different contexts,
        // we can use a single execution method if we differentiate via internal state.
        // However, to keep it clean, the negative scenarios call the specific 'executed with violation' step
        // or we handle the logic here. 
        // For simplicity, this method is the entry point for the positive scenario. 
        // The negative scenarios explicitly call the method above in their steps (if we configured it that way).
        // To avoid ambiguity, we'll rely on the specific methods above or check context here.
        
        if (aggregate.id().contains("fail")) {
            the_start_session_cmd_command_is_executed_with_violation();
        } else {
            the_start_session_cmd_command_is_executed();
        }
    }
}
