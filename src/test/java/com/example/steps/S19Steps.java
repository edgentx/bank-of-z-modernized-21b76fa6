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

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state for happy path: Authenticated and Active
        aggregate.markAuthenticated();
        aggregate.setLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Default state is isAuthenticated=false, so no explicit action needed
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Authenticated
        // Set last activity to 31 minutes ago (Timeout is 30)
        aggregate.setLastActivity(Instant.now().minus(31, java.time.temporal.ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        String sessionId = "session-context-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("MAIN_MENU"); // User is already at MAIN_MENU
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by aggregate construction in Given steps
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in When step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            String targetMenu = "TARGET_MENU";
            String action = "ENTER";
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            thrownException = e;
        }
    }

    @When("the NavigateMenuCmd command is executed with same context")
    public void the_NavigateMenuCmd_command_is_executed_with_same_context() {
        // Explicitly attempt to navigate to the menu already set in the aggregate (MAIN_MENU)
        try {
            String targetMenu = "MAIN_MENU";
            String action = "ENTER";
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // In this domain, RuntimeException subtypes are considered domain errors for validation
        Assertions.assertTrue(thrownException instanceof IllegalStateException || 
                              thrownException instanceof IllegalArgumentException);
    }
}
