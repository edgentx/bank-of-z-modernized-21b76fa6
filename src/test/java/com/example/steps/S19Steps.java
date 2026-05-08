package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markValid(); // Ensure invariants are satisfied
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly handled in the aggregate constructor
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be set in command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be set in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            command = new NavigateMenuCmd("session-123", "DEPOSIT_SCREEN", "ENTER", true, true);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    // Scenario 2: Auth Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-404");
        // Do not mark valid -> remains unauthenticated
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_unauth() {
        try {
            // Command explicitly sets authenticated to false to simulate the violation
            command = new NavigateMenuCmd("session-404", "MAIN_MENU", "ENTER", false, true);
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario 3: Timeout Failure
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_timeout() {
        try {
            // Command sets active to false to simulate timeout
            command = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER", true, false);
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_timeout() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("timeout"));
    }

    // Scenario 4: Navigation State Failure
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        aggregate.markValid();
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_context() {
        try {
            // We use a special menuId "INVALID_CONTEXT" to trigger the invariant logic
            command = new NavigateMenuCmd("session-bad-ctx", "INVALID_CONTEXT", "ENTER", true, true);
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_context() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("operational context"));
    }
}
