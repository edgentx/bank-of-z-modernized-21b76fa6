package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state for 'Valid' scenario
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate construction, just ensuring we have one
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // This is handled in the 'When' step construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // This is handled in the 'When' step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        // Defaults for success scenario
        String menuId = "MAIN_MENU";
        String action = "ENTER";
        String sessionId = aggregate.id();
        
        if (cmd == null) {
             cmd = new NavigateMenuCmd(sessionId, menuId, action);
        }
        
        try {
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
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated
        // Prepare command
        cmd = new NavigateMenuCmd(sessionId, "DASHBOARD", "ENTER");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago (Default timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        
        cmd = new NavigateMenuCmd(sessionId, "DASHBOARD", "ENTER");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        String sessionId = "session-context-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set a context that is invalid for the action (simulated via setter in aggregate)
        aggregate.setContext("INVALID_CONTEXT");
        
        cmd = new NavigateMenuCmd(sessionId, "NEXT_MENU", "FORBIDDEN_ACTION");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Check it's the specific exception types defined in the aggregate
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException
        );
    }
}
