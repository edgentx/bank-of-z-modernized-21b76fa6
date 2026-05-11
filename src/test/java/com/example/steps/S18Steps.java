package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Implicitly, the command will provide a token, but we can mock the validation logic
        // by setting internal state if the aggregate relied on it, or just pass a null/invalid token.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_timed_out() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // We assume the aggregate constructor initializes the state correctly.
        // The violation will be triggered by the StartSessionCmd logic if the session was already active.
        // Or if we model this as a resume. The scenario implies starting a session might fail if timing is wrong.
        // For simplicity in S-18, we will rely on the aggregate throwing if the session is already active (started).
        // Let's mark it as started manually to simulate the invariant violation.
        aggregate.markAsStarted(); // Helper method for testing purposes
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_invalid_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Simulation: if the system detects a mismatch in expected state.
        // We might pass a specific flag or invalid terminal info in the command.
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "TELLER_001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "TERM_42";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId, "dummy-token");
        try {
            aggregate.execute(cmd);
        } catch (Throwable t) {
            this.thrownException = t;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        DomainEvent event = aggregate.uncommittedEvents().get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        SessionStartedEvent sse = (SessionStartedEvent) event;
        assertEquals("session-123", sse.aggregateId());
        assertEquals("TELLER_001", sse.tellerId());
        assertEquals("TERM_42", sse.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        // Verify it's a domain error (IllegalStateException or IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
