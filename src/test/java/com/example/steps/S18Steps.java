package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String currentTellerId;
    private String currentTerminalId;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("ts-1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Simulate lack of authentication by not setting an authenticated state or forcing an invalid state.
        // In this simplified in-memory model, the Command execution handles the check.
        aggregate = new TellerSessionAggregate("ts-invalid-auth");
        // No context set up, ensuring "isAuthenticated" would return false in a real handler.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Simulate an existing active session that timed out
        aggregate = new TellerSessionAggregate("ts-timeout");
        // Force state to ACTIVE or TIMED_OUT to simulate the violation
        aggregate.markAsTimedOut(); // Helper method to set state for test purposes
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Simulate invalid navigation state
        aggregate = new TellerSessionAggregate("ts-nav-error");
        aggregate.markNavigationStateInvalid(); // Helper method for test setup
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.currentTellerId = "teller-123";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.currentTerminalId = "term-TX-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Construct the command
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId, Instant.now());
            // Execute
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected list of events, but got null");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Domain logic errors are typically RuntimeExceptions (IllegalStateException, IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof RuntimeException, "Expected RuntimeException domain error");
    }
}
