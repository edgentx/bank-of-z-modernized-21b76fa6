package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // --- Scenario Setup ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state
        aggregate.markAuthenticated();
        aggregate.setCurrentScreen("HOME");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in command construction below
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in command construction below
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command construction below
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-unauth");
        // Default state is authenticated=false, which violates the invariant for navigation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Force the timestamp back to simulate timeout
        aggregate.forceTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        this.aggregate = new TellerSessionAggregate("session-context");
        aggregate.markAuthenticated();
        // Set current screen to TARGET. Trying to navigate to where you already are violates context.
        aggregate.setCurrentScreen("ACCOUNT_INQUIRY");
    }

    // --- Execution ---

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            String targetMenu = "ACCOUNT_INQUIRY";
            // Adjust target for the context violation scenario to force the error
            if ("ACCOUNT_INQUIRY".equals(aggregate.getCurrentScreenId())) {
                targetMenu = "ACCOUNT_INQUIRY";
            }

            this.command = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            this.resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    // --- Assertions ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
        Assertions.assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // We expect IllegalStateException for invariant violations, IllegalArgumentException for context/validation errors
        Assertions.assertTrue(
                thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
                "Expected domain error but got: " + thrownException.getMessage()
        );
    }
}
