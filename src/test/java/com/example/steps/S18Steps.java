package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId = "sess-123";
    private String tellerId = "teller-001";
    private String terminalId = "term-42";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // tellerId defaults to valid
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // terminalId defaults to valid
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Construct valid command defaults
        StartSessionCmd cmd = new StartSessionCmd(
                sessionId,
                tellerId,
                terminalId,
                true,  // authenticated
                false, // stale
                "READY" // navigationState
        );
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_start_session_cmd_command_is_executed_with_invalid_auth() {
        StartSessionCmd cmd = new StartSessionCmd(
                sessionId, tellerId, terminalId,
                false, // NOT authenticated
                false, "READY"
        );
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with stale context")
    public void the_start_session_cmd_command_is_executed_with_stale_context() {
        StartSessionCmd cmd = new StartSessionCmd(
                sessionId, tellerId, terminalId,
                true,
                true, // Stale
                "READY"
        );
        executeCommand(cmd);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void the_start_session_cmd_command_is_executed_with_invalid_nav_state() {
        StartSessionCmd cmd = new StartSessionCmd(
                sessionId, tellerId, terminalId,
                true,
                false,
                "INVALID_STATE" // Bad nav state
        );
        executeCommand(cmd);
    }

    private void executeCommand(StartSessionCmd cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
