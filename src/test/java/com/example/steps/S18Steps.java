package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_A01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        // Setup valid defaults for invariants if not explicitly violated
        aggregate.markAuthenticated(true);
        aggregate.setTimeoutConfigured(true);
        aggregate.setNavigationState("IDLE");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the When step via constants
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the When step via constants
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Construct valid command based on defaults set in 'Given valid'
        boolean isAuthenticated = true; // Assumed valid unless scenario overrides
        boolean isTimeoutConfigured = true; // Assumed valid unless scenario overrides

        // If the aggregate was prepped in a specific state by a 'Given that violates' step,
        // we might want to reflect that in the command if the command carries dynamic flags.
        // For this BDD, we assume the command triggers the flow against the aggregate state.

        Command cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID, isAuthenticated, isTimeoutConfigured);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("SESSION_FAIL_AUTH");
        // State: Not authenticated
        aggregate.markAuthenticated(false);
        aggregate.setTimeoutConfigured(true); // Valid
        aggregate.setNavigationState("IDLE"); // Valid
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_FAIL_TIMEOUT");
        // State: Timeout not configured
        aggregate.markAuthenticated(true); // Valid
        aggregate.setTimeoutConfigured(false); // Invalid
        aggregate.setNavigationState("IDLE"); // Valid
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("SESSION_FAIL_NAV");
        // State: Not IDLE (e.g., already ACTIVE or in ERROR)
        aggregate.markAuthenticated(true); // Valid
        aggregate.setTimeoutConfigured(true); // Valid
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS"); // Invalid for start
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }

}
