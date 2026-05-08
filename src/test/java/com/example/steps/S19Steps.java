package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Step Definitions for S-19 (NavigateMenuCmd).
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(15);

    // In-memory repository mock behavior can be simulated here if needed, 
    // but we can instantiate the aggregate directly for unit testing logic.

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Create a valid session, authenticated and active
        aggregate = new TellerSessionAggregate("sess-123", DEFAULT_TIMEOUT);
        aggregate.markAuthenticated("teller-001"); // Authenticated
        // Session is fresh (created just now), so it is active
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("sess-unauth", DEFAULT_TIMEOUT);
        // Intentionally do NOT call markAuthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("sess-timeout", DEFAULT_TIMEOUT);
        aggregate.markAuthenticated("teller-001");
        aggregate.markExpired(); // Helper to move lastActivity into the past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("sess-blocked", DEFAULT_TIMEOUT);
        aggregate.markAuthenticated("teller-001");
        aggregate.markContextBlocked(); // Helper to set state to BLOCKED
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization in Given steps, 
        // but we ensure the command we create uses it.
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Validated implicitly when command is executed
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Validated implicitly
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Domain errors in this DDD style are RuntimeExceptions (IllegalStateException, etc.)
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
