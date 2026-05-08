package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a prior login to set authenticated state and last activity
        aggregate.login("teller-001"); 
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in the aggregate initialization
        Assertions.assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no error, but got: " + capturedException);
        List<DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "No events were emitted");
        Assertions.assertTrue(events.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        Assertions.assertEquals(menuId, event.menuId());
        Assertions.assertEquals(action, event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Deliberately do not call login() to ensure isAuthenticated is false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.login("teller-001");
        // Force the last activity time to be far in the past
        aggregate.forceLastActivityTime(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        sessionId = "sess-invalid-state";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.login("teller-001");
        // Set state to a mode where navigation is logically invalid (e.g. Maintenance mode)
        aggregate.setCurrentContext("MAINTENANCE_MODE");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain error to be thrown");
        // We expect IllegalStateException for business rule violations
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException, 
            "Expected domain error (IllegalStateException/IllegalArgumentException), but got: " + capturedException.getClass().getSimpleName());
    }
}
