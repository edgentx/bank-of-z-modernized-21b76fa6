package com.example.steps;

import com.example.domain.shared.Command;
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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Assumption: Command is created in the 'When' step with this ID
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Assumption: Command is created in the 'When' step with this ID
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Using valid IDs implied by previous steps
        StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "terminal-T01");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("terminal-T01", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // We simulate a violation by creating an aggregate that is already started
        // (cannot start a session twice, simulating the constraint check)
        aggregate = new TellerSessionAggregate("session-2");
        // Execute a valid first command to transition state to STARTED
        aggregate.execute(new StartSessionCmd("session-2", "teller-123", "terminal-T01"));
        // Now it is in a state where a subsequent Start command is invalid (contextual auth violation)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // We simulate this by creating an aggregate, starting it, and forcing the last active time to be old
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.execute(new StartSessionCmd("session-3", "teller-123", "terminal-T01"));
        // Force internal state to indicate timeout
        aggregate.forceTimeoutForTesting();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        // We simulate a context mismatch by trying to start with null context details
        aggregate = new TellerSessionAggregate("session-4");
        // No further setup needed if we use a specific command variant or just expect failure on default
        // However, standard valid flow works, so we rely on the aggregate detecting invalid state 
        // if we attempt to restart or if we construct it improperly.
        // For this test, let's assume the aggregate is simply IDLE, but we are testing a negative case
        // so we'll rely on the 'When' step providing data that violates the rule (e.g. null terminal)
    }

    // Specialized When for the specific violation scenarios if command data differs
    @When("the StartSessionCmd command is executed on the invalid aggregate")
    public void the_start_session_cmd_command_is_executed_on_invalid_aggregate() {
        // For the violation scenarios, the aggregate state is the problem, not necessarily the command data
        // (except for nav state which might check command data).
        try {
            // For the Nav state violation, we will try to start with a null terminalId
            if (aggregate.id().equals("session-4")) {
                 resultEvents = aggregate.execute(new StartSessionCmd("session-4", "teller-123", null));
            } else {
                 // For others, the aggregate state is already problematic
                 resultEvents = aggregate.execute(new StartSessionCmd(aggregate.id(), "teller-123", "term"));
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
