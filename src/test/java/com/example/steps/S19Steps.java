package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-001"); // Ensure it's valid/authenticated
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate creation or command construction
        if (aggregate == null) {
            aggregate = new TellerSessionAggregate("session-123");
            aggregate.markAuthenticated("teller-001");
        }
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Context setup
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context setup
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create aggregate but DO NOT authenticate
        aggregate = new TellerSessionAggregate("session-unauth");
        // aggregate.markAuthenticated(...) is skipped intentionally
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-002");
        // Force timeout
        aggregate.expireSession();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated("teller-003");
        // The violation will be triggered by passing a null/blank menuId in the When step
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        // Default to valid command params if not specified by violation context
        String menuId = (aggregate != null && aggregate.getCurrentMenuId() != null) ? "MAIN_MENU" : "MAIN_MENU";
        
        // Check if we are testing the context violation (we need a null menuId)
        // Simple check: if the aggregate ID matches the violation setup, pass bad data
        if ("session-bad-context".equals(aggregate.id())) {
            menuId = ""; // Blank context
        }

        cmd = new NavigateMenuCmd(aggregate.id(), menuId, "ENTER");
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals("MAIN_MENU", navEvent.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
