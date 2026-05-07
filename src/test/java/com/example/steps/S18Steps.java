package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAsAuthenticated(); // Assume authenticated for valid case
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Teller ID is part of the command construction, handled in 'When'
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Terminal ID is part of the command construction, handled in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default to valid data if not set by specific violation context
        String tellerId = "teller-1";
        String terminalId = "TERM-01";
        command = new StartSessionCmd(tellerId, terminalId);
        
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        Assertions.assertEquals("session.started", resultingEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.markAsUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAsAuthenticated();
        aggregate.markAsStale(); // Helper method in aggregate to simulate old timestamp
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAsAuthenticated();
    }

    // Override the When step for the negative case to trigger the violation logic if needed,
    // or rely on the aggregate state setup above.
    // For the Navigation state violation, we need to send a BAD terminalId in the command.
    @When("the StartSessionCmd command is executed with invalid context")
    public void the_start_session_cmd_command_is_executed_with_invalid_context() {
        // Use a terminal ID that violates the validation rule (e.g., not TERM-*)
        command = new StartSessionCmd("teller-1", "INVALID-TERM");
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Cucumber will match this regex to the scenario steps
    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_generic() {
        // Re-use standard execution logic for other failures (Auth, Timeout)
        // But for the specific "Navigation state" scenario, Cucumber might pick the wrong one if we aren't careful.
        // However, based on the scenarios provided:
        // "Navigation state..." -> uses specific invalid context step.
        // The rest use standard execution.
        
        // If we are in the Nav state scenario, the command was already created in the specific When hook.
        // But Cucumber hooks execute in order.
        // Let's unify: The default When handles Auth and Timeout because they rely on AGGREGATE state.
        // The Nav scenario relies on COMMAND state.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}