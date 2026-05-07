package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to reset state between scenarios if needed (though Cucumber usually instantiates new)
    private void reset() {
        aggregate = null;
        command = null;
        resultEvents = null;
        thrownException = null;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        reset();
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure base valid state
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in command creation below or via default
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in command creation
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Default command values for positive path
            String sid = aggregate != null ? aggregate.id() : "session-unknown";
            if (command == null) {
                command = new NavigateMenuCmd(sid, "MAIN_MENU", "ENTER");
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        reset();
        // Create aggregate but DO NOT mark authenticated
        aggregate = new TellerSessionAggregate("session-unauth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        reset();
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markSessionExpired(); // Push time back
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        reset();
        aggregate = new TellerSessionAggregate("session-corrupt");
        aggregate.markAuthenticated();
        aggregate.markCorrupted(); // Simulate state lock
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // We check for IllegalStateException which is how we enforce domain rules
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
