package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute StartSessionCmd

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("TS-123");
        // Default to valid state (authenticated, idle)
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityTime(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the When block construction for simplicity, or stored here
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            command = new StartSessionCmd("TS-123", "TELLER-01", "TERM-99");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("TS-401");
        aggregate.markUnauthenticated(); // Violation
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityTime(Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
        assertTrue(caughtException.getMessage().contains("authenticated"), "Error message should mention authentication");
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("TS-402");
        aggregate.markAuthenticated();
        // Set time to 20 minutes ago (Default timeout is 15)
        aggregate.setLastActivityTime(Instant.now().minus(20, java.time.temporal.ChronoUnit.MINUTES));
        // We also need to simulate it being 'active' or 'in session' for the logic to trigger a check
        // In the aggregate logic provided, it checks 'isActive' implicitly via time diff if active.
        // The implementation provided checks time diff regardless of 'isActive' flag if lastActivity is set.
    }

    // Scenario: StartSessionCmd rejected — Navigation state

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("TS-403");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IN_TRANSACTION"); // Violation (Should be IDLE)
        aggregate.setLastActivityTime(Instant.now());
    }
}