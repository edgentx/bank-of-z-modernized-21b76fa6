package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId = "session-123";
    private String menuId = "main-menu";
    private String action = "SELECT";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with valid state
        aggregate.hydrate("teller-1", "previous-menu", Instant.now(), true);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by setup
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled by setup
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled by setup
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // isAuthenticated defaults to false in constructor, or explicit here:
        aggregate.hydrate("teller-1", "login-screen", Instant.now(), false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Set last activity to 31 minutes ago (Threshold is 30)
        Instant past = Instant.now().minus(Duration.ofMinutes(31));
        aggregate.hydrate("teller-1", "timeout-screen", past, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Trying to navigate to the menu we are already on
        String stuckMenu = "stuck-menu";
        aggregate.hydrate("teller-1", stuckMenu, Instant.now(), true);
        this.menuId = stuckMenu; // Attempting to navigate to current menu
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Typically domain errors are IllegalStateExceptions or specific DomainExceptions
        assertTrue(capturedException instanceof IllegalStateException);
    }
}