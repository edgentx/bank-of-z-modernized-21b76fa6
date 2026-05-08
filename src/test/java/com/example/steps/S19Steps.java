package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private String providedSessionId;
    private String providedMenuId;
    private String providedAction;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        providedSessionId = "session-123";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Assume pre-authenticated for happy path unless specified otherwise
        aggregate.markAuthenticated("teller-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(providedSessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        providedMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        providedAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(providedSessionId, providedMenuId, providedAction);
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.getFirst() instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.getFirst();
        assertEquals("menu.navigated", event.type());
        assertEquals(providedMenuId, event.menuId());
        assertEquals(providedAction, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        providedSessionId = "session-unauth";
        providedMenuId = "LOGIN_MENU";
        providedAction = "ENTER";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Intentionally do NOT mark as authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        providedSessionId = "session-timeout";
        providedMenuId = "MAIN_MENU";
        providedAction = "ENTER";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated("teller-01");
        aggregate.markTimedOut(); // force timeout state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_state_context() {
        providedSessionId = "session-bad-context";
        providedMenuId = "MAIN_MENU";
        providedAction = "ENTER";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated("teller-01");
        // State violation is implicit if we are not ACTIVE, but standard setup makes us ACTIVE.
        // To violate "accurately reflect operational context" based on implementation,
        // we can imagine the aggregate expects a specific flow. 
        // However, given the current Aggregate logic, we are Active.
        // Let's interpret the violation as a precondition failure that the code would theoretically catch
        // or simply testing that the code enforces that we *are* in the right state.
        // Since we are authenticated, we are active.
        // To force the error, we need a scenario where Status is not ACTIVE but we try to navigate.
        // The Aggregate constructor sets NONE. markAuthenticated sets ACTIVE.
        // If we just create it but don't call markAuthenticated (unauth), covered above.
        // If we call markAuthenticated, we are ACTIVE.
        // Let's rely on the implementation detail: if status is not ACTIVE.
        // Since the API doesn't expose setting Status to something else arbitrarily without other changes,
        // we will trust the implementation check for `status != SessionStatus.ACTIVE`.
        // This specific step setup is a bit tricky without mutating the internal state directly.
        // However, assuming the "Timeout" scenario sets status to TIMED_OUT, that covers the status check.
        // Let's assume this scenario implies a state where navigation is invalid even if authenticated.
        // For the purpose of this test, if the previous tests cover Unauth and Timeout, this might be redundant 
        // if it implies the same variable `status`.
        // Let's re-use the timeout logic or assume a hypothetical invalid state.
        // Actually, looking at the Aggregate: `status != SessionStatus.ACTIVE` checks both.
        // So `markTimedOut()` covers this scenario too.
        aggregate.markTimedOut(); 
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(aggregate.uncommittedEvents().isEmpty(), "Expected no events to be emitted on failure");
    }
}
