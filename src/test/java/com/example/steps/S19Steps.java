package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to simulate repository reconstruction (hydration)
    private TellerSessionAggregate loadAggregate(String id) {
        return new TellerSessionAggregate(id);
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = loadAggregate("session-123");
        // Simulate the aggregate being in a valid, authenticated, active state
        aggregate.activateSession("teller-456", Instant.now().plusSeconds(300)); // Active for 5 mins
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = loadAggregate("session-auth-fail");
        // Aggregate is created but never activated, or logged out explicitly
        // aggregate.revokeSession(); // Optional: if explicit logout state exists
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = loadAggregate("session-timeout");
        // Activate session but set expiration in the past
        aggregate.activateSession("teller-456", Instant.now().minusSeconds(10)); // Expired 10s ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = loadAggregate("session-bad-state");
        aggregate.activateSession("teller-456", Instant.now().plusSeconds(300));
        // The aggregate is currently at a Menu 'A', but the command tries to transition to a state 
        // that is invalid from 'A' (e.g. screen navigation logic violation)
        // Or simply that the screen required by the command doesn't match the current screen.
        // We simulate this by not updating the internal state of the aggregate to match the command's requirements.
        // For the aggregate logic, we assume it checks getCurrentMenuId() vs target.
        aggregate.setMenu("MAIN_MENU"); // Current state
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization in other Givens, or used to lookup aggregate
        // In this unit test context, the aggregate ID is the Session ID.
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Data setup for the command
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Data setup for the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        Command cmd;
        
        // Context specific command creation based on the scenario setup
        if (aggregate.getId().equals("session-bad-state")) {
             // Scenario: Navigation state violation
             // We are at MAIN_MENU, but trying to enter a detail screen that requires 'ACCT_SELECT'
             cmd = new NavigateMenuCmd("session-bad-state", "ACCT_DETAILS", "ENTER");
        } else {
             // Standard Success Command
             cmd = new NavigateMenuCmd(aggregate.getId(), "NEXT_MENU", "ENTER");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("NEXT_MENU", event.targetMenu());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // Depending on specific business rule violation, exception type might vary
        // Generic check for domain logic exception
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException
        );
    }
}
