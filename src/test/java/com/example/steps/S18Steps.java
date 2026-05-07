package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellering.model.SessionStartedEvent;
import com.example.domain.tellering.model.StartSessionCmd;
import com.example.domain.tellering.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("ts-1");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // In a real test, we might set context here, but the command carries the ID.
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd("ts-1", "user-01", "term-01", "MAIN_MENU", Instant.now().plusSeconds(300));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not throw exception: " + thrownException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // Scenario 2: Auth Error
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("ts-2");
        // We simulate the violation by passing an invalid auth flag in the command
    }

    // When is shared

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // Verify it's a domain error (IllegalStateException for invariant violations)
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("Teller must be authenticated") 
            || thrownException.getMessage().contains("Session timeout")
            || thrownException.getMessage().contains("Navigation state"));
    }

    // Scenario 3: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("ts-3");
        // We simulate the violation by passing an expiration date in the past
    }

    // Scenario 4: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("ts-4");
        // We simulate the violation by passing a null/invalid navigation state
    }

    // Specific When for error cases to trigger the specific violation logic in the steps
    @When("the StartSessionCmd command is executed with null navigation state")
    public void execute_invalid_nav() {
        StartSessionCmd cmd = new StartSessionCmd("ts-4", "user-01", "term-01", null, Instant.now().plusSeconds(300));
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the StartSessionCmd command is executed with expired timeout")
    public void execute_expired() {
        StartSessionCmd cmd = new StartSessionCmd("ts-3", "user-01", "term-01", "MAIN_MENU", Instant.now().minusSeconds(10));
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the StartSessionCmd command is executed with unauthenticated user")
    public void execute_unauthenticated() {
        // Passing authenticated=false via constructor logic implied or overload
        // Since StartSessionCmd takes boolean, we use it directly
        StartSessionCmd cmd = new StartSessionCmd("ts-2", "user-01", "term-01", "MAIN_MENU", Instant.now().plusSeconds(300), false);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

}