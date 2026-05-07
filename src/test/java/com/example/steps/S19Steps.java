package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "S-19-SESSION-01";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Setup valid state
        aggregate.setLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "S-19-SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // markAuthenticated() is NOT called.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "S-19-SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markInactive(); // Sets time in the past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        sessionId = "S-19-SESSION-CONTEXT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setContextInvalid(); // Sets currentMenuId to LOCKED
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId is already initialized in the Given blocks
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "ACCOUNT_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown exception: " + thrownException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
    }
}