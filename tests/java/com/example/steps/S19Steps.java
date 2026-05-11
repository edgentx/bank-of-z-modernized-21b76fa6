package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> events;
    private Throwable caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Using hydrate method to construct a valid aggregate state per Critical feedback requirement
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.hydrate(
            true,                  // authenticated
            Instant.now(),         // lastActivity (now, so not timed out)
            "MAIN_MENU",          // currentMenu
            "ACTIVE"              // operationalContext
        );
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.hydrate(
            false,                 // authenticated = FALSE
            Instant.now(),
            "MAIN_MENU",
            "ACTIVE"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        // Simulate a session last active 31 minutes ago (assuming 30 min timeout)
        Instant pastActivity = Instant.now().minus(Duration.ofMinutes(31));
        aggregate.hydrate(
            true,
            pastActivity,
            "MAIN_MENU",
            "ACTIVE"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-409");
        // Invalid state: Operational Context is LOCKED, but user is trying to navigate
        aggregate.hydrate(
            true,
            Instant.now(),
            "MAIN_MENU",
            "LOCKED"  // Not ACTIVE
        );
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Encoded in the aggregate instantiation
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be passed in the command
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be passed in the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("ACCOUNTS_SUBMENU", "SELECT");
            events = aggregate.execute(cmd);
        } catch (DomainException | IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should emit exactly one event");
        assertTrue(events.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        assertEquals("ACCOUNTS_SUBMENU", event.targetMenuId());
        assertEquals("SELECT", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Verify it's a specific domain logic error (IllegalStateException is standard for invariants)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}