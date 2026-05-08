package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.MenuNavigatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-" + UUID.randomUUID();
        // Create a valid session in 'ACTIVE' state, authenticated, and not timed out
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Seed state via protected setters or prior commands to ensure valid context
        aggregate.setAuthenticated(true);
        aggregate.setSessionStart(Instant.now().minusSeconds(60)); // Started 60s ago
        aggregate.setSessionTimeout(Duration.ofMinutes(15)); // Timeout after 15m
        aggregate.setCurrentMenuId("MAIN_MENU"); // Valid context
        aggregate.setState(TellerSessionAggregate.SessionState.ACTIVE);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in aggregate initialization
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "ACCOUNTS_SUBMENU";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(false); // Not authenticated
        aggregate.setSessionStart(Instant.now());
        aggregate.setSessionTimeout(Duration.ofMinutes(15));
        aggregate.setState(TellerSessionAggregate.SessionState.ACTIVE);
        aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setSessionStart(Instant.now().minus(Duration.ofHours(1))); // Started 1 hour ago
        aggregate.setSessionTimeout(Duration.ofMinutes(15)); // Timeout configured for 15m
        aggregate.setState(TellerSessionAggregate.SessionState.ACTIVE);
        aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setSessionStart(Instant.now());
        aggregate.setSessionTimeout(Duration.ofMinutes(15));
        aggregate.setState(TellerSessionAggregate.SessionState.ACTIVE);
        aggregate.setCurrentMenuId("UNKNOWN_MENU_STATE"); // Context implies invalid transition source or state
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.targetMenuId());
        assertEquals(action, event.actionTaken());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected a domain exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
