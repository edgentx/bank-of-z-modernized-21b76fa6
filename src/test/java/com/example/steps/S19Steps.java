package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private String sessionId;
    private String menuId;
    private String action;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = "SES-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state (authenticated)
        aggregate.setAuthenticated(true);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = "SES-401";
        aggregate = new TellerSessionAggregate(sessionId);
        // Default constructor sets authenticated = false, but explicit is clearer
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "SES-408";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true); // Valid auth
        aggregate.expireSession(); // Trigger timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        // This scenario is handled by providing invalid inputs in the 'When' step below.
        // The aggregate itself is valid, but the command data is not.
        sessionId = "SES-400";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in setup
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // If specific violation setup didn't define menuId/action, use defaults that might fail
            if (menuId == null) menuId = "INVALID"; 
            if (action == null) action = "PRESS";
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Expected no error, but got: " + caughtException.getMessage());
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected at least one event");
        assertTrue(events.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent evt = (MenuNavigatedEvent) events.get(0);
        assertEquals("menu.navigated", evt.type());
        assertEquals(sessionId, evt.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // The domain logic throws IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Expected domain error (IllegalStateException or IllegalArgumentException), got: " + caughtException.getClass().getSimpleName());
    }
}
