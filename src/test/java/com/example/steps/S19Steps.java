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
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup for validity: authenticated and active
        aggregate.markAuthenticated("teller-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate initialization
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Used in command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Used in command construction
    }

    // --- Violation Givens ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "session-violation-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly leave unauthenticated
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-violation-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01");
        // Force timeout
        aggregate.markAsTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        String sessionId = "session-violation-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsInvalidContext(); // Sets internal state to trigger violation
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        // Constructing command. Values are arbitrary for positive flow,
        // for violations, state inside aggregate determines failure.
        command = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals("MAIN_MENU", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // In this domain logic, violations throw IllegalStateException
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        System.out.println("Expected error caught: " + thrownException.getMessage());
    }
}
