package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.command.StartSessionCmd;
import com.example.domain.tellersession.event.SessionStartedEvent;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private String sessionId = "session-123";
    private String validTellerId = "teller-01";
    private String validTerminalId = "term-01";
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession(sessionId);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup, used in the When step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        executeCommand(true, Instant.now(), false);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Should have emitted events");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(validTellerId, event.tellerId());
        assertEquals(validTerminalId, event.terminalId());
    }

    // Scenario: Authentication Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession(sessionId);
        // The violation will be in the command (authenticated = false)
    }

    @When("the StartSessionCmd command is executed with no auth")
    public void the_start_session_cmd_command_is_executed_with_no_auth() {
        executeCommand(false, Instant.now(), false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("authenticated") || 
                   capturedException instanceof IllegalStateException);
    }

    // Scenario: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession(sessionId);
    }

    @When("the StartSessionCmd command is executed with stale context")
    public void the_start_session_cmd_command_is_executed_with_stale_context() {
        // Simulating a scenario where the system context provided is considered stale/error
        // In this specific domain logic, the error injection is handled via the 'isTerminalInError' flag for scenario simulation
        // Or we can modify the aggregate logic. For this BDD, we'll rely on the navigation state error flag to simulate rejection.
        executeCommand(true, Instant.now().minusSeconds(1000), false);
    }

    // Scenario: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession(sessionId);
    }

    @When("the StartSessionCmd command is executed with invalid context")
    public void the_start_session_cmd_command_is_executed_with_invalid_context() {
        executeCommand(true, Instant.now(), true);
    }

    // Helper to execute command and capture results
    private void executeCommand(boolean authenticated, Instant occurredAt, boolean terminalInError) {
        try {
            Command cmd = new StartSessionCmd(validTellerId, validTerminalId, authenticated, occurredAt, terminalInError);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
