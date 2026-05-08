package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.InvalidNavigationContextException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup an authenticated, active session
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate authenticated login
        aggregate.markAuthenticated("teller-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The sessionId is part of the aggregate ID, implicitly handled
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When clause via Command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When clause via Command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Default valid command execution
            Command cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Scenarios with specific pre-conditions ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally do NOT call markAuthenticated(). The state defaults to unauthenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-01");
        // Simulate timeout by setting last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.forceLastActivityTime(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.markAuthenticated("teller-01");
        // Set state to LOCKED, which is invalid for navigation
        aggregate.setCurrentState(TellerSessionAggregate.SessionState.LOCKED);
    }

    // --- Variations of When for violation scenarios ---

    @When("the NavigateMenuCmd command is executed with target \"([^\"]+)\"")
    public void the_navigate_menu_cmd_command_is_executed_with_target(String targetMenu) {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Assertions ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected a domain error exception, but command succeeded");
        assertTrue(
            thrownException instanceof IllegalStateException || 
            thrownException instanceof InvalidNavigationContextException,
            "Expected domain error, got: " + thrownException.getClass().getSimpleName()
        );
    }
}
