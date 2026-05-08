package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.command.StartSessionCmd;
import com.example.domain.tellersession.event.SessionStartedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "sess-123";
    private String validTellerId = "teller-001";
    private String validTerminalId = "term-A";

    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Valid ID set in field initialization
        assertNotNull(validTellerId);
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Valid ID set in field initialization
        assertNotNull(validTerminalId);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // By default in the 'happy path' scenario, we construct a valid command
        executeCommand(true, false, true);
    }

    private void executeCommand(boolean isAuthenticated, boolean isStaleContext, boolean isNavValid) {
        Command cmd = new StartSessionCmd(
                sessionId,
                validTellerId,
                validTerminalId,
                isAuthenticated,
                isStaleContext,
                isNavValid
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(validTellerId, event.tellerId());
        assertEquals(validTerminalId, event.terminalId());
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    // Specific When contexts for failures

    @When("the StartSessionCmd command is executed with missing auth")
    public void the_start_session_cmd_command_is_executed_with_missing_auth() {
        executeCommand(false, false, true);
    }

    @When("the StartSessionCmd command is executed with stale context")
    public void the_start_session_cmd_command_is_executed_with_stale_context() {
        executeCommand(true, true, true);
    }

    @When("the StartSessionCmd command is executed with invalid navigation")
    public void the_start_session_cmd_command_is_executed_with_invalid_navigation() {
        executeCommand(true, false, false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        // Ensure no events were emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty());
    }
}