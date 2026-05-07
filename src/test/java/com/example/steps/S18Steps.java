package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Helper to create a basic valid aggregate
    private TellerSessionAggregate createValidAggregate() {
        // We create an aggregate. The 'execute' method handles state transitions.
        // To test 'valid' scenarios, we instantiate a fresh aggregate ID.
        return new TellerSessionAggregate("session-123");
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = createValidAggregate();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // No-op in this context, handled in When
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // No-op in this context, handled in When
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Use valid defaults for the success case
            StartSessionCmd cmd = new StartSessionCmd(
                "session-123",
                "teller-01",
                "term-42",
                Instant.now().plus(Duration.ofHours(8)), // Valid timeout in future
                "HOME" // Valid state
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("teller-01", event.tellerId());
        Assertions.assertEquals("term-42", event.terminalId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed with violation data")
    public void the_start_session_cmd_command_is_executed_with_violation_data() {
        try {
            // Determine which scenario we are in based on the aggregate ID (hacky but works for simple steps)
            String id = aggregate.id();
            String tellerId = "teller-01";
            String terminalId = "term-42";
            Instant timeout = Instant.now().plus(Duration.ofHours(8));
            String state = "HOME";

            if (id.contains("auth-fail")) {
                // Invalid: null teller ID
                tellerId = null;
            } else if (id.contains("timeout-fail")) {
                // Invalid: timeout in past
                timeout = Instant.now().minusSeconds(60);
            } else if (id.contains("nav-fail")) {
                // Invalid: null/invalid state
                state = null;
            }

            StartSessionCmd cmd = new StartSessionCmd(id, tellerId, terminalId, timeout, state);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception but command succeeded");
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, got: " + caughtException.getClass()
        );
    }
}
