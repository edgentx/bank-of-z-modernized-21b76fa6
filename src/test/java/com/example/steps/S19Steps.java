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
        aggregate = new TellerSessionAggregate("session-123");
        // Initialize state as valid: authenticated, active, context consistent
        aggregate.applyStateForTest(new TellerSessionInitializedEvent("session-123", "teller-1", Instant.now()));
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is intrinsic to the aggregate ID in this context, or verified in execute
        // Handled by aggregate ID in setup
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When block via Command constructor
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When block via Command constructor
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("MAIN_MENU", event.menuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // State implies not authenticated (e.g. no initialized event or flag set to false)
        // We leave it uninitialized to simulate lack of auth context
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Initialize with a timestamp older than allowed (e.g. 31 minutes ago)
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(31));
        aggregate.applyStateForTest(new TellerSessionInitializedEvent("session-timeout", "teller-1", oldTime));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        aggregate.applyStateForTest(new TellerSessionInitializedEvent("session-bad-ctx", "teller-1", Instant.now()));
        // Simulate a state where the teller is trying to jump to a menu that is inaccessible from current state
        // This is a bit abstract for the aggregate without complex state logic, so we might simulate
        // the aggregate being in a 'locked' or 'invalid-context' state.
        // For this exercise, we will assume the aggregate enforces strict context flow.
        aggregate.markContextInvalidForTest();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected exception but command succeeded");
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        System.out.println("Correctly rejected with: " + thrownException.getMessage());
    }
}
