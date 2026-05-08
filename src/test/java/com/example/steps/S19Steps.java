package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private static final String SESSION_ID = "sess-123";
    private static final String MENU_ID = "MAIN_MENU";
    private static final String ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in constructor/command creation
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in command creation
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        executeCommand(
            SESSION_ID,
            MENU_ID,
            ACTION,
            true,   // authenticated
            true,   // session active
            "VALID_CONTEXT", // valid context
            "VALID_CONTEXT"  // matches requested
        );
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals(MENU_ID, event.menuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @When("the NavigateMenuCmd command is executed on unauthenticated session")
    public void the_NavigateMenuCmd_command_is_executed_unauthenticated() {
        executeCommand(
            SESSION_ID,
            MENU_ID,
            ACTION,
            false,  // NOT authenticated
            true,
            "VALID_CONTEXT",
            "VALID_CONTEXT"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @When("the NavigateMenuCmd command is executed on timed out session")
    public void the_NavigateMenuCmd_command_is_executed_timed_out() {
        executeCommand(
            SESSION_ID,
            MENU_ID,
            ACTION,
            true,
            false,  // Session NOT active (Timed out)
            "VALID_CONTEXT",
            "VALID_CONTEXT"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_NavigateMenuCmd_command_is_executed_invalid_context() {
        executeCommand(
            SESSION_ID,
            MENU_ID,
            ACTION,
            true,
            true,
            "VALID_CONTEXT",      // Current aggregate state
            "INVALID_CONTEXT"     // Requested state (Simulating violation)
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Should be IllegalStateException");
        Assertions.assertNotNull(resultEvents, "Result list should exist");
        Assertions.assertTrue(resultEvents.isEmpty(), "No events should be emitted on failure");
    }

    private void executeCommand(String sessionId, String menuId, String action,
                                boolean isAuthenticated, boolean isSessionActive,
                                String currentState, String requestedContext) {
        this.caughtException = null;
        this.cmd = new NavigateMenuCmd(sessionId, menuId, action, isAuthenticated, isSessionActive, currentState, requestedContext);
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
            this.resultEvents = List.of();
        }
    }
}
