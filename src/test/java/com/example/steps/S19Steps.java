package com.example.steps;

import com.example.domain.shared.Command;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentSessionId;
    private String currentMenuId;
    private String currentAction;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        currentSessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // By default, we assume valid context for success scenario
        aggregate.markAsAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        currentSessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Intentionally do NOT mark as authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        currentSessionId = "SESSION-TIMEDOUT";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAsAuthenticated();
        aggregate.expireSession(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        currentSessionId = "SESSION-BAD-CONTEXT";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAsAuthenticated();
        // Setup a context where the action doesn't make sense (e.g. missing params or invalid state)
        // We will feed it a blank action in the 'When' step or via setup
        currentMenuId = ""; 
        currentAction = "";
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the aggregate constructor in the Given steps
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        currentMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        currentAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), currentMenuId, currentAction);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals(MenuNavigatedEvent.class, resultEvents.get(0).getClass());
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(currentMenuId, event.menuId());
        assertEquals(currentAction, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We expect IllegalStateException for invariants or IllegalArgumentException for context
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
