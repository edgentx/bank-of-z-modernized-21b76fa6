package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // deliberately not calling markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        aggregate.markExpired(); // Simulate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-400");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenuId("ACCOUNT_DETAIL");
        // Trying to navigate to the same menu will violate the context rule defined in the aggregate
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the aggregate creation
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in command creation
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        String currentMenu = (aggregate.getCurrentMenuId() != null) ? aggregate.getCurrentMenuId() : "START";
        String targetMenu = "TRANSACTIONS";
        
        // For the context violation test, we try to navigate to the same menu we are already at
        if ("ACCOUNT_DETAIL".equals(currentMenu)) {
            targetMenu = "ACCOUNT_DETAIL";
        }

        Command cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "SELECT");
        try {
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
        assertEquals("menu.navigated", event.type());
        assertEquals("TRANSACTIONS", event.getMenuId());
        assertNull(thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        assertNull(resultEvents); // Should not have produced events if invariant failed
    }
}
