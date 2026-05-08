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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private String sessionId = "session-123";
    private String menuId = "MAIN_MENU";
    private String action = "DISPLAY";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Aggregate is initialized, authenticated, and active
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate an active session state
        aggregate.setAuthenticated(true);
        aggregate.setLastActivity(Instant.now());
        aggregate.setCurrentContextId("CTX_DEFAULT");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(false); // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        // Violation: Last activity was 1 hour ago (assuming timeout is 15 mins)
        aggregate.setLastActivity(Instant.now().minus(Duration.ofHours(1)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivity(Instant.now());
        // Violation: Context is locked or invalid (null is treated as invalid here)
        aggregate.setCurrentContextId(null);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Using default sessionId
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Using default menuId
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Using default action
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals(sessionId, event.aggregateId());
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(menuId, event.menuId());
        Assertions.assertEquals(action, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // In this domain logic, we use standard Java exceptions (IllegalStateException/IllegalArgumentException)
        // to represent domain errors.
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException
        );
    }
}
