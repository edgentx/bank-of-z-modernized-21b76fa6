package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to create a fresh valid aggregate for the "Given a valid TellerSession aggregate" step
    private TellerSessionAggregate createValidAggregate() {
        TellerSessionAggregate agg = new TellerSessionAggregate("session-123");
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = createValidAggregate();
        assertNotNull(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Assumes we are building the command, or that the aggregate is in a state to accept it.
        // In this pattern, we often instantiate the command in the 'When' step, but here we assume valid data setup.
        // We will construct the command fully in the When step using valid data constants.
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // See above.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        // Put in a state where authentication fails (e.g. null token or invalid context)
        // Since we are executing StartSession, the aggregate likely checks a flag or context provided in the command.
        // We will simulate this by passing an empty authToken in the Command.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a session that is already active but timed out, or a command that implies an invalid timeout.
        // Based on the story "StartSessionCmd", this likely means we are trying to start a session with invalid timeout config.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Simulate invalid context (e.g. branch mismatch)
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // We construct the command based on the scenario context inferred from the Given steps.
        // Ideally, we'd store scenario state, but for simplicity, we infer violations based on the aggregate ID.
        
        String id = aggregate.id();
        
        if (id.equals("session-bad-auth")) {
             command = new StartSessionCmd(id, "teller-1", "terminal-1", "INVALID_TOKEN");
        } else if (id.equals("session-timeout")) {
             // Duration.ZERO or negative violates timeout requirements
             command = new StartSessionCmd(id, "teller-1", "terminal-1", "VALID_TOKEN", Duration.ZERO);
        } else if (id.equals("session-nav-error")) {
             // Null context violates nav state
             command = new StartSessionCmd(id, "teller-1", "terminal-1", "VALID_TOKEN", Duration.ofMinutes(30), null);
        } else {
             // Valid defaults
             command = new StartSessionCmd(id, "teller-1", "terminal-1", "VALID_TOKEN");
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
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain errors in this repo are typically RuntimeExceptions like IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}