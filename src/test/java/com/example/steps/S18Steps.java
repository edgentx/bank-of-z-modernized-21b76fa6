package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Simple in-memory repo mock (or could use InMemoryTellerSessionRepository if it existed)
    // For this exercise, we instantiate aggregate directly to keep file count managed.

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.caughtException = null;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in 'When' step construction for simplicity
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command scenario
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            "teller-01",
            "terminal-42",
            Instant.now(), // Authenticated just now
            "READY"       // Valid navigation state
        );
        executeCommand(cmd);
    }

    // --- Specific Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    // --- Execution for Violations ---

    @When("the StartSessionCmd command is executed with missing tellerId")
    public void the_start_session_cmd_command_is_executed_with_missing_teller_id() {
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            null, // Violation
            "terminal-42",
            Instant.now(),
            "READY"
        );
        executeCommand(cmd);
    }

    @When("the StartSessionCmd command is executed with expired authentication")
    public void the_start_session_cmd_command_is_executed_with_expired_auth() {
        // Authenticated 20 minutes ago (Timeout is 15)
        Instant past = Instant.now().minusSeconds(1200); 
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            "teller-01",
            "terminal-42",
            past, // Violation
            "READY"
        );
        executeCommand(cmd);
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_start_session_cmd_command_is_executed_with_invalid_nav_state() {
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            "teller-01",
            "terminal-42",
            Instant.now(),
            "INVALID_STATE" // Violation
        );
        executeCommand(cmd);
    }

    // --- Assertions ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals("teller-01", event.tellerId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verify it's an explicit domain logic exception (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException);
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
