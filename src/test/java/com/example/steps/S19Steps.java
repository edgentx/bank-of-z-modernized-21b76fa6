package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private java.util.List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Default to authenticated for valid state
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId initialized in previous step
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        menuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void a valid_action_is_provided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = "unauth-session";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated
        a_valid_menuId_is_provided();
        a_valid_action_is_provided();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "timeout-session";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.expireSession(); // Force timeout
        a_valid_menuId_is_provided();
        a_valid_action_is_provided();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        sessionId = "bad-state-session";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        a_valid_action_is_provided();
        // Set invalid menuId explicitly
        menuId = ""; // Blank/Invalid context
    }
}
