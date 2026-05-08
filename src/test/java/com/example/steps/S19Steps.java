package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "sess-123";
        // Create an authenticated, active session
        aggregate = new TellerSessionAggregate(
            sessionId,
            "teller-1",
            true,  // authenticated
            "MAIN_MENU", // currentMenu
            Instant.now() // lastActivity
        );
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Already initialized in the aggregate setup
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        menuId = "ACCOUNT_INQUIRY";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals(menuId, event.menuId());
        Assertions.assertEquals(action, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "sess-unauth";
        // Not authenticated
        aggregate = new TellerSessionAggregate(
            sessionId,
            null,
            false, // not authenticated
            null,
            Instant.now()
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "sess-timeout";
        // Authenticated, but last activity was 31 minutes ago (configured timeout is 30m)
        aggregate = new TellerSessionAggregate(
            sessionId,
            "teller-1",
            true,
            "MAIN_MENU",
            Instant.now().minus(Duration.ofMinutes(31))
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        sessionId = "sess-bad-context";
        aggregate = new TellerSessionAggregate(
            sessionId,
            "teller-1",
            true,
            "MAIN_MENU",
            Instant.now()
        );
        // Simulate bad context via invalid action command later, or setup aggregate state here if necessary.
        // For this invariant, we'll rely on passing a blank action in the When step to trigger the logic,
        // but we could also set internal state if the aggregate tracked valid transitions strictly.
        // Here we simulate by forcing the action to be bad in the scenario logic.
    }

    // Specific override for context violation to trigger the specific error logic
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_navigate_menu_cmd_command_is_executed_with_invalid_context() {
        try {
            // Passing blank action to violate operational context
            Command cmd = new NavigateMenuCmd(sessionId, "SOME_MENU", "");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Depending on the invariant, it could be IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

}
