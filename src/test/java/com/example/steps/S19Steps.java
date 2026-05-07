package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSession aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // We start by creating a session via the Initiate command to satisfy invariants
        this.sessionId = "TS-123";
        InitiateTellerSessionCmd initCmd = new InitiateTellerSessionCmd(
            sessionId, 
            "U-999", 
            "MAIN_MENU", 
            Instant.now().plusSeconds(3600) // 1 hour timeout
        );
        this.aggregate = new TellerSession(sessionId);
        // Apply initiation events directly to set state without persisting
        List<DomainEvent> events = aggregate.execute(initCmd);
        // Mark state as applied by simulating event handling or internal state update if needed
        // For this in-memory test, execute() updates state.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in 'a valid TellerSession aggregate'
        Assertions.assertNotNull(this.sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "ACCOUNT_SUMMARY";
    }

    @Given("a valid action is provided")
    public void a valid_action_is_provided() {
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "TS-FAIL-AUTH";
        this.aggregate = new TellerSession(sessionId);
        // Aggregate remains in uninitialized state (authenticated = false)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "TS-FAIL-TIMEOUT";
        // Create a valid session first
        InitiateTellerSessionCmd initCmd = new InitiateTellerSessionCmd(
            sessionId, "U-999", "MAIN_MENU", Instant.now().minusSeconds(10) // Expired
        );
        this.aggregate = new TellerSession(sessionId);
        aggregate.execute(initCmd);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        this.sessionId = "TS-FAIL-CONTEXT";
        // Valid auth
        InitiateTellerSessionCmd initCmd = new InitiateTellerSessionCmd(
            sessionId, "U-999", "MAIN_MENU", Instant.now().plusSeconds(3600)
        );
        this.aggregate = new TellerSession(sessionId);
        aggregate.execute(initCmd);

        // Attempt to navigate to a menu that is not accessible from MAIN_MENU
        this.menuId = "ADMIN_SUPER_USER";
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // If not set by specific violation context, set defaults
            if (menuId == null) this.menuId = "ACCOUNT_SUMMARY";
            if (action == null) this.action = "ENTER";

            Command cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(this.sessionId, event.aggregateId());
        Assertions.assertEquals(this.menuId, event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception");
        // Verify it's one of our domain errors (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error, got: " + caughtException.getClass().getSimpleName()
        );
    }
}