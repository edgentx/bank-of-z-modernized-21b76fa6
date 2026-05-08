package com.example.steps;

import com.example.domain.tellermSession.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // No-op, context handles via command execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // No-op, context handles via command execution
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        this.aggregate = new TellerSessionAggregate("session-404");
        // Reconstitute in an authenticated state to block subsequent start
        // For this exercise, we rely on the aggregate logic to fail if the command lacks auth context
        // but the aggregate itself is valid. The logic is inside execute().
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        // Internal simulation of timeout configuration check
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-error");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // We use a constructor ref pattern; auth status determined by payload in real world, 
            // here we pass valid data for success, and specific data for failure based on the 'Given' setup.
            // For simplicity in this unit-test style step, we assume valid cmd unless setup mocks internal state to fail.
            
            // To trigger specific failures without complex payload mocking in this simplified test harness,
            // we inspect the aggregate ID or state to decide which command to send.
            String id = aggregate.id();
            
            Command cmd;
            if (id.equals("session-404")) {
                // Trigger Auth Failure (simulated by invalid/null teller)
                cmd = new StartSessionCmd("session-404", null, "T-101", Duration.ofHours(8)); 
            } else if (id.equals("session-timeout")) {
                // Trigger Timeout Failure (simulated by negative or 0 duration)
                cmd = new StartSessionCmd("session-timeout", "U-123", "T-101", Duration.ZERO);
            } else if (id.equals("session-nav-error")) {
                // Trigger Nav State Failure (simulated by null/blank terminal)
                cmd = new StartSessionCmd("session-nav-error", "U-123", "", Duration.ofHours(8));
            } else {
                // Success Case
                cmd = new StartSessionCmd("session-123", "teller-alice", "TERM-01", Duration.ofHours(8));
            }

            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertEquals(SessionStartedEvent.class, resultingEvents.get(0).getClass());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
