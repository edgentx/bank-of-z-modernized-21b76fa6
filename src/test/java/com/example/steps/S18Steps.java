package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private Exception caughtException;
    private DomainEvent resultEvent;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Teller ID provided in the command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Terminal ID provided in the command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-1", "term-1", true, false, true);
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvent, "Expected an event to be emitted");
        assertTrue(resultEvent instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvent;
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-1", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with missing authentication")
    public void execute_unauthenticated_cmd() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-1", "term-1", false, false, true);
        executeCommand(cmd);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with timeout status")
    public void execute_timeout_cmd() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-1", "term-1", true, true, true);
        executeCommand(cmd);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void execute_invalid_nav_cmd() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-1", "term-1", true, false, false);
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }

    private void executeCommand(Command cmd) {
        caughtException = null;
        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = events.get(0);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }
}
