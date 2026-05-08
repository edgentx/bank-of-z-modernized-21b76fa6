package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private Exception caughtException;
    private String currentSessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        currentSessionId = "TS-123";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated(); // Setup valid state
        aggregate.setNavigationState("IDLE");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the 'When' step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            command = new StartSessionCmd(currentSessionId, "TELLER-01", "TERM-05");
            aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        DomainEvent event = aggregate.uncommittedEvents().get(0);
        assertInstanceOf(SessionStartedEvent.class, event);
        assertEquals("session.started", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_a_teller_must_be_authenticated_to_initiate_a_session() {
        currentSessionId = "TS-FAIL-AUTH";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Do NOT mark authenticated. Default is false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_sessions_must_timeout_after_a_configured_period_of_inactivity() {
        currentSessionId = "TS-FAIL-TIMEOUT";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
        aggregate.expireSession(); // Helper method to set last activity in the past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state_must_accurately_reflect_the_current_operational_context() {
        currentSessionId = "TS-FAIL-NAV";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated();
        aggregate.setNavigationState("TXN_PENDING"); // Not IDLE
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
