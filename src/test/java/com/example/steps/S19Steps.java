package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermession.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private String sessionId = "session-123";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a valid session state (Authenticated, Active, Valid State)
        // We use reflection or package-private access in a real test setup, 
        // but here we assume the constructor or a factory would set this up.
        // For this exercise, we'll simulate a 'fresh' valid session.
        
        // To make it valid, we apply a Created Event internally to set state
        var event = new TellerSessionCreatedEvent(sessionId, "teller-1", Instant.now());
        // In a real repo we would hydrate, here we manually set state or apply event
        // Assuming a testing-friendly way to set state for BDD:
        aggregate.setTestingState("teller-1", Instant.now().minusSeconds(60), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate(sessionId);
        // State: Authenticated = false/null
        aggregate.setTestingState(null, Instant.now(), null); 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // State: Last Active time > 15 mins ago (or configured period)
        aggregate.setTestingState("teller-1", Instant.now().minusSeconds(2000), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        // State: Trying to navigate to a screen not allowed from current screen
        // e.g. current is MAIN_MENU, action requires ACCOUNT_SUMMARY context
        aggregate.setTestingState("teller-1", Instant.now(), "LOCKED_SCREEN");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is initialized in constructor
        Assertions.assertEquals(sessionId, aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(sessionId, "ACCOUNT_SUMMARY", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no error, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("ACCOUNT_SUMMARY", event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error but command succeeded");
        // We expect IllegalStateException or IllegalArgumentException based on our impl
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException ||
            caughtException instanceof UnknownCommandException
        );
    }
}
