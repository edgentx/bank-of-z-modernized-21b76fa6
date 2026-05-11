package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> events;
    private Exception exception;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // In a real scenario, we would hydrate the aggregate to a valid state.
        // For BDD setup, we assume the default constructor creates a valid base.
        // We can simulate authentication via a bootstrap command or direct state reflection if exposed,
        // but here we rely on the command handler logic for the success path assuming valid state.
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Contextual setup - handled in the 'When' step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Contextual setup - handled in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd("teller-123", "terminal-456");
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(events);
        Assertions.assertFalse(events.isEmpty());
        Assertions.assertTrue(events.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("teller-123", event.tellerId());
        Assertions.assertEquals("terminal-456", event.terminalId());
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Simulate a state where the teller is not authenticated.
        // Since we don't have a 'login' command implemented yet to set state, 
        // we assume the command handler checks for a flag or context. 
        // For this BDD test, we pass a command with null/empty auth token if the contract required it.
        // Here we simulate it by how we construct the command or the aggregate internal state logic.
        // Assuming the aggregate starts unauthenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Assuming the aggregate has state that tracks last activity.
        // We would hydrate an aggregate with an old timestamp.
        // For stub implementation, we might pass a specific flag or check data.
        aggregate = new TellerSessionAggregate("session-3", true, false, false);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Simulate invalid navigation state
        aggregate = new TellerSessionAggregate("session-4", false, true, false);
    }

    // Note: In a strict BDD setup, we often reuse the 'When' step.
    // However, to trigger specific failure modes in this stub, we might need
    // to pass different command parameters or set up the aggregate differently.
    // For simplicity in this stub, we will assume the Command carries the intent
    // or the Aggregate State carries the violation.
    
    // Re-using the When step for the failure cases implies the logic inside execute() detects the violation.
    
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof DomainException);
        Assertions.assertTrue(exception.getMessage().contains("rejected"));
    }
}