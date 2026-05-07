package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String currentMenuId;
    private String currentAction;
    private String currentSessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        currentSessionId = "sess-123";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-001"); // Ensure authenticated state
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate initialization
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        currentMenuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        currentAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        currentSessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Do NOT authenticate
        currentMenuId = "MAIN_MENU";
        currentAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        currentSessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-001");
        // Force expiration
        aggregate.markExpired();
        currentMenuId = "MAIN_MENU";
        currentAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        currentSessionId = "sess-invalid-state";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-001");
        // Setting an invalid menuId (blank) to trigger the state validation logic
        currentMenuId = "";
        currentAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(currentSessionId, currentMenuId, currentAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(currentMenuId, event.menuId());
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We check for IllegalStateException or IllegalArgumentException as per domain logic
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}