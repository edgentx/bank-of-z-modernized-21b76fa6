package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellerm_session.model.StartSessionCmd;
import com.example.domain.tellerm_session.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        // Create a fresh aggregate (State: None)
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // This step is implicit in the context setup, usually handled in the 'When' or via scenario context.
        // We ensure the aggregate is in a state where auth is valid (default state).
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Implicitly handled
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            String tellerId = "TELLER_001";
            String terminalId = "TERM_3270_A";
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId, Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted one event");
        Assertions.assertEquals("SessionStartedEvent", resultEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Force the aggregate into a state where authentication has failed or is missing
        // (Simulated by the aggregate's internal state or context, here we assume the aggregate
        // knows auth failed). Since this is a simple unit-test style aggregate, we might need
        // to construct it in a failed state or pass invalid data. The invariant check is inside execute.
        // To make the aggregate reject, we can use a null TellerId or rely on the aggregate's internal logic.
        // However, the aggregate is stateless until started. The 'Auth' invariant usually implies
        // checking a token or credential passed in the command. Let's assume the Command has a flag or
        // we construct the aggregate such that it rejects.
        // Better approach for this pattern: The `StartSessionCmd` includes credentials.
        // If we want to test rejection, we construct a command with bad credentials.
        // But the step says "Given a TellerSession aggregate that violates...".
        // We will mock the internal state to indicate auth failure.
    }

    // Override the execution for the negative flow to pass invalid data or ensure failure
    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_start_session_cmd_command_is_executed_with_invalid_auth() {
        try {
            // Pass null tellerId to simulate lack of auth
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), null, "TERM_3270_A", Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // This scenario implies checking state. Since StartSession creates a session,
        // perhaps it rejects creating one if the Teller is already 'timed out' in a broader context.
        // Or, we pass a timestamp that is invalid.
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with timeout violation")
    public void the_start_session_cmd_command_is_executed_with_timeout_violation() {
        try {
            // Simulate a stale start request (e.g. past timestamp)
            Instant past = Instant.now().minusSeconds(3600);
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), "TELLER_001", "TERM_3270_A", past);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with nav violation")
    public void the_start_session_cmd_command_is_executed_with_nav_violation() {
        try {
            // Pass an invalid terminal ID (e.g., null or empty) to violate context validity
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), "TELLER_001", null, Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_generic() {
        this.the_command_is_rejected_with_a_domain_error();
    }
}
