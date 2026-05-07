package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // The command is constructed in the When step, this step ensures data availability
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // The command is constructed in the When step, this step ensures data availability
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Defaults for the happy path
        command = new StartSessionCmd("session-123", "teller-abc", "terminal-xyz", true);
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-abc", event.tellerId());
        assertEquals("terminal-xyz", event.terminalId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAsTimedOut(); // Helper to set state to TIMED_OUT
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markNavigationStateInvalid(); // Helper to set state invalid
    }

    // Reuse When step, but modify behavior via private state or specific command overrides if needed.
    // To support different commands for negative tests without breaking the simple Step flow,
    // we check the specific context in the When block or use a flag.
    // Here, we can inspect the aggregate state to decide what command to send.
    
    // Refactoring the When step slightly to handle command construction based on aggregate state is complex in pure Cucumber.
    // We will overload When clauses for clarity in BDD style.

    @When("the StartSessionCmd command is executed with authentication=false")
    public void the_start_session_cmd_command_is_executed_unauthenticated() {
        command = new StartSessionCmd("session-auth-fail", "teller-abc", "terminal-xyz", false);
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the StartSessionCmd command is executed on timed out session")
    public void the_start_session_cmd_command_is_executed_on_timed_out_session() {
        command = new StartSessionCmd("session-timeout", "teller-abc", "terminal-xyz", true);
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the StartSessionCmd command is executed on invalid navigation state")
    public void the_start_session_cmd_command_is_executed_on_invalid_nav_state() {
        command = new StartSessionCmd("session-nav-fail", "teller-abc", "terminal-xyz", true);
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        // Check message content if strict validation needed
    }

}
