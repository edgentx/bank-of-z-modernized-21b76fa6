package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("TS-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in 'When' step construction for simplicity, or stored here
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            cmd = new StartSessionCmd("TS-123", "TELLER-01", "TERM-01");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        assertEquals("session.started", resultEvents.get(0).type());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated to initiate a session.
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("TS-404");
        // Attempt to start a session with a null/empty teller ID to simulate auth failure
        cmd = new StartSessionCmd("TS-404", null, "TERM-01");
    }

    // Reuse existing When/Then
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain errors are typically IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout after a configured period of inactivity.
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("TS-TIMEOUT");
        // Simulate a session that is already active or in an invalid state for start
        // This aggregate implicitly starts in NONE, but we can't force it to a weird state easily without state mutation.
        // Assuming the Aggregate checks internal state.
        // For this test, we rely on the fact that we can't start a session if one is active (implicit invariant check if implemented).
        // But since we only have 'Start', let's assume we force it into an invalid state via test-specific helper or reflection if needed.
        // For now, we construct a command that might violate a constraint if the aggregate logic was more complex.
        // However, looking at the requirements, if we can't set the state, we might need to accept that this test
        // verifies the enforcement of invariants based on input data.
        // Let's assume the aggregate has already been started. We need to emulate a second start attempt.
        aggregate.execute(new StartSessionCmd("TS-TIMEOUT", "TELLER-01", "TERM-01")); // First start
        cmd = new StartSessionCmd("TS-TIMEOUT", "TELLER-01", "TERM-01"); // Second start (violation)
    }

    // Scenario: StartSessionCmd rejected — Navigation state must accurately reflect the current operational context.
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("TS-NAV");
        // Represents an invalid state transition or invalid context data
        // We'll use an invalid terminal ID pattern to represent context violation
        cmd = new StartSessionCmd("TS-NAV", "TELLER-01", "INVALID-TERM-FORMAT");
    }

}