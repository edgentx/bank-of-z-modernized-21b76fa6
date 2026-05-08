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
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Pre-authenticate for the 'valid' scenario to pass invariants
        aggregate.authenticate("teller-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId already set in 'Given'
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER"; // Valid action per aggregate logic
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = "sess-unauth";
        // Create aggregate but DO NOT authenticate it (default constructor sets authenticated=false)
        aggregate = new TellerSessionAggregate(sessionId);
        menuId = "MAIN_MENU";
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("teller-01"); // Authenticate
        aggregate.markInactive(); // Force timeout
        menuId = "MAIN_MENU";
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        sessionId = "sess-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("teller-01");
        menuId = "MAIN_MENU";
        action = "INVALID_ACTION"; // Action that fails validation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect either IllegalStateException or IllegalArgumentException depending on invariant
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}