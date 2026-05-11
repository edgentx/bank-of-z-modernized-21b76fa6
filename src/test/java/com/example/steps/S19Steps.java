package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate to a valid authenticated state
        aggregate.hydrate(true, Instant.now(), "MAIN_MENU", "VIEW");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is already set in the Given step
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        menuId = "ACCOUNT_SUMMARY";
        assertNotNull(menuId);
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER";
        assertNotNull(action);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with authenticated = false
        aggregate.hydrate(false, Instant.now(), "LOGIN_SCREEN", "PROMPT");
        menuId = "ADMIN_MENU";
        action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with a timestamp older than 15 minutes
        Instant oldTime = Instant.now().minus(20, ChronoUnit.MINUTES);
        aggregate.hydrate(true, oldTime, "MAIN_MENU", "VIEW");
        menuId = "ACCOUNT_SUMMARY";
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context_match() {
        sessionId = "session-mismatch";
        aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated and active
        aggregate.hydrate(true, Instant.now(), "MAIN_MENU", "VIEW");
        
        // Setup valid menu/action, but we will use a DIFFERENT session ID in the command 
        // to trigger the context mismatch error logic if implemented strictly, 
        // OR (as per standard aggregate routing) we rely on the aggregate ID check.
        // In this implementation, we check ID equality inside the command handler.
        menuId = "ACCOUNT_SUMMARY";
        action = "ENTER";
        
        // Simulate sending a command for a different session to this aggregate instance
        sessionId = "OTHER_SESSION_ID"; 
    }
}
