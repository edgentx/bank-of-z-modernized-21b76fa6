package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: TellerSession Navigation.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Scenario Setup
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Ensure it is authenticated for the 'Success' case
        aggregate.execute(new TellerLoggedInCmd("teller-01"));
        aggregate.clearEvents(); // Clear login events to isolate navigation events
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate initialization in previous step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Context setup, specific IDs used in the 'When' step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context setup
    }

    // Execution
    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            var cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Success Outcome
    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Deliberately do NOT call execute(TellerLoggedInCmd)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.execute(new TellerLoggedInCmd("teller-01"));
        aggregate.clearEvents();
        
        // Simulate time passage: Set last activity to 20 minutes ago
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_operational_context() {
        aggregate = new TellerSessionAggregate("session-context-error");
        aggregate.execute(new TellerLoggedInCmd("teller-01"));
        aggregate.clearEvents();
        
        // Simulate context error
        aggregate.markOperationalContextInvalid();
    }

    // Failure Outcome
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We verify it's a domain logic error (IllegalStateException)
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
