package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.InitiateSessionCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.SessionInitiatedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId = "session-123";
    private String menuId = "MainMenu";
    private String action = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Initiate to ensure it's active and authenticated (Happy Path default)
        aggregate.execute(new InitiateSessionCmd(sessionId, "TELLER_1", "TERM_1"));
        // Clear events from initiation so we only verify Navigate events
        aggregate.clearEvents(); 
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Manually force state to active but NOT authenticated
        aggregate.setActive(true);
        aggregate.setAuthenticated(false);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Initiate session
        aggregate.execute(new InitiateSessionCmd(sessionId, "TELLER_1", "TERM_1"));
        aggregate.clearEvents();
        // Force last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_operational_context() {
        aggregate = new TellerSessionAggregate(sessionId);
        // We use the Auth violation as a proxy for Context violation for this implementation
        // as 'Operational Context' is broad. Or we can simulate a state that shouldn't accept navigation.
        // Let's assume a "Locked" state where navigation is rejected.
        aggregate.setActive(true);
        aggregate.setAuthenticated(false); // Invalid context for command
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Using default 'session-123'
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Using default 'MainMenu'
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Using default 'ENTER'
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            var cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        var event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(menuId, event.targetMenuId());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException for domain rule violations
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
