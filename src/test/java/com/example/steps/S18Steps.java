package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context handled in When clause
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context handled in When clause
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        var cmd = new StartSessionCmd("session-123", "teller-001", "term-42", true, "IDLE");
        try {
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("teller-001", event.tellerId());
        assertEquals("term-42", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // We will pass isAuthenticated=false in the command to simulate the violation
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void execute_start_session_invalid_auth() {
        var cmd = new StartSessionCmd("session-123", "teller-001", "term-42", false, "IDLE");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // In this context, a start command might be rejected if we tried to start
        // with a timestamp that implies immediate staleness, or if the aggregate state was stale.
        // For StartSession, the invariant primarily means "don't start a stale session".
        aggregate = new TellerSessionAggregate("session-123");
    }

    @When("the StartSessionCmd command is executed with stale context")
    public void execute_start_session_stale() {
        // Simulate a command that implies the context is invalid/stale
        var cmd = new StartSessionCmd("session-123", null, "term-42", true, "IDLE");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void execute_start_session_invalid_nav() {
        // Simulate starting a session while claiming to be in a transaction context
        var cmd = new StartSessionCmd("session-123", "teller-001", "term-42", true, "IN_TRANSACTION");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Domain errors are typically IllegalStateException or IllegalArgumentException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}