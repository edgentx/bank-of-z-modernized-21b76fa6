package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Initialize valid state
        aggregate.initSession("teller-456", "MAIN_MENU");
        aggregate.clearEvents();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Assumed to be 'session-123' set in previous step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "DEPOSIT_SCREEN", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("DEPOSIT_SCREEN", event.targetMenuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        // Not initializing, so state is null/unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.initSession("teller-456", "MAIN_MENU");
        aggregate.simulateTimeout();
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.initSession("teller-456", "MAIN_MENU");
        aggregate.markContextInvalid();
        aggregate.clearEvents();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected exception but command succeeded");
        // The exact exception type varies (IllegalStateException, IllegalArgumentException, etc.)
        Assertions.assertTrue(caughtException instanceof RuntimeException);
    }

    // Ensure we have a basic test suite runner if not present elsewhere
    // @Suite
    // @SelectClasspathResource("features")
    // @IncludeEngines("cucumber")
    // public class S19TestSuite {}
}
