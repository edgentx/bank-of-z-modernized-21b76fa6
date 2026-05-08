package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Initialize an authenticated, active session
        String sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate initialization to valid state (normally via InitSessionCmd)
        // For this test, we assume the constructor creates a valid base
        aggregate.initializeSession("user-123", Instant.now().plus(Duration.ofHours(1)));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do not initialize; isAuthenticated remains false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initialize with an expiry time in the past
        aggregate.initializeSession("user-123", Instant.now().minus(Duration.ofMinutes(5)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        // This implies a business rule that might prevent certain navigations if state is inconsistent.
        // To simulate this, we might set a state flag manually or assume the aggregate detects a mismatch.
        // For now, we will create a valid session and then perhaps force a mismatch if the model supported it.
        // However, based on the implementation, we will throw an error if the requested menu is invalid relative to context.
        String sessionId = "sess-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initializeSession("user-123", Instant.now().plus(Duration.ofHours(1)));
        // We will rely on the command parameters in the 'When' step to trigger this violation
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in the aggregate setup
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the 'When' step command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the 'When' step command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Determine parameters based on the scenario setup
            String menuId = "MAIN_MENU";
            String action = "ENTER";

            // For the 'invalid context' scenario, we might pass a specific bad input if logic requires
            if (aggregate.id().equals("sess-context")) {
                 // Example: navigating to a screen that is not allowed from the current screen
                 // For this simple implementation, we assume any non-null menu is valid unless business rules say otherwise
                 // To trigger the 'context' error in our implementation, we might check for a specific mismatch.
                 // Since the implementation is simple, we will try to navigate to a restricted screen.
                 menuId = "SCREEN_NOT_ALLOWED_FROM_CURRENT_STATE";
            }

            Command cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Check if it's the specific exception type or a generic illegal state/argument
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}