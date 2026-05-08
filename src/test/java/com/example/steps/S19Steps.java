package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd.
 * Tests invariants: Authentication, Timeout, Operational Context.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Throwable thrownException;
    private String sessionId;
    private String menuId;
    private String action;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it is authenticated by default for the success case
        aggregate.markAuthenticated("TELLER-001");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT authenticate. The default constructor creates an unauthenticated session.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-002");
        // Force the aggregate to think it has timed out
        aggregate.markSessionTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        sessionId = "SESSION-BAD-CONTEXT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-003");
        // Force invalid operational context
        aggregate.markOperationalContextInvalid();
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled implicitly by aggregate creation or specific step variables
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER";
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
        try {
            aggregate.execute(cmd);
        } catch (Throwable t) {
            thrownException = t;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have at least one event");
        assertTrue(events.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");

        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // We expect IllegalStateException based on our implementation, or a custom DomainException.
        // The existing pattern uses IllegalStateException.
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify the message matches the invariant violations
        String msg = thrownException.getMessage();
        assertTrue(msg.contains("authenticated") || msg.contains("timeout") || msg.contains("context"), 
            "Error message should relate to the specific invariant violation: " + msg);
    }
}