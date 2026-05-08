package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to simulate aggregate creation logic encapsulated in tests
    private TellerSessionAggregate createSession(boolean authenticated, Instant lastActivity, String currentMenu) {
        // Directly instantiate or use factory. Assuming public constructor for test access
        // or relying on a simulated hydrated state.
        // For simplicity in this test suite, we assume a constructor that allows hydration.
        // In a real scenario, you might hydrate via applyEvents.
        return new TellerSessionAggregate("session-1", authenticated, lastActivity, currentMenu);
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Valid: Authenticated, Active, Consistent State
        aggregate = createSession(true, Instant.now(), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Violation: Not authenticated
        aggregate = createSession(false, Instant.now(), "LOGIN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // Violation: Last activity was > 30 minutes ago (Threshold assumed as 30m)
        Instant ancientTime = Instant.now().minus(Duration.ofMinutes(35));
        aggregate = createSession(true, ancientTime, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        // Violation: Current context is 'LOCKED' or 'OFFLINE', but teller tries to nav to 'MAIN_MENU'
        aggregate = createSession(true, Instant.now(), "TERMINAL_LOCKED");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // ID is hardcoded in aggregate setup for this scenario, handled implicitly
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Command construction handles this
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Command construction handles this
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Construct command with valid defaults for positive flow
            // Specific violation cases rely on Aggregate internal state check
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-1", "DEPOSIT_SCREEN", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // In DDD, this is usually an IllegalArgumentException or IllegalStateException
        Assertions.assertTrue(IllegalArgumentException.class.isInstance(capturedException) 
            || IllegalStateException.class.isInstance(capturedException));
    }
}
