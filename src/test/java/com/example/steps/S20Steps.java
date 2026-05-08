package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> result;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Simulate an active session state (e.g., via previous command or state transition logic)
        // Assuming the aggregate defaults to ACTIVE or we transition it here.
        // For test simplicity, we assume the aggregate is created and managed externally or supports state initialization.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate construction in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // To simulate this violation, we would likely transition the aggregate to a state
        // where it cannot be ended, or mock the auth check.
        // However, based on standard patterns, if we just try to end a session that never started/isn't authenticated,
        // it should fail.
        // For the sake of this test, we assume the aggregate tracks authentication.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Simulate timeout state logic if applicable
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        // Simulate invalid navigation state logic if applicable
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            this.result = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof SessionEndedEvent);
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception, but command succeeded.");
        // Optionally assert specific exception types (e.g., IllegalStateException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
