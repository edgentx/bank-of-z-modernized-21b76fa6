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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String testSessionId = "session-123";
    private String testMenuId = "MAIN_MENU";
    private String testAction = "ENTER";

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(testSessionId);
        // Setup defaults for a valid aggregate
        aggregate.markAuthenticated("teller-001");
        aggregate.setCurrentScreen("WELCOME");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in initialization
        assertNotNull(testSessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in initialization
        assertNotNull(testMenuId);
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in initialization
        assertNotNull(testAction);
    }

    // --- Violation Givens ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(testSessionId);
        // Do NOT authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated("teller-001");
        // Set last activity to 20 minutes ago (Timeout is default 15m)
        Instant past = Instant.now().minus(20, ChronoUnit.MINUTES);
        aggregate.setLastActivityAt(past);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated("teller-001");
        // Set context to a state where navigation is blocked (e.g. LOCKED screen)
        aggregate.setCurrentScreen("LOCKED");
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(testSessionId, testMenuId, testAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(testMenuId, event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain rules violations usually manifest as IllegalStateException in this model
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage() != null && !caughtException.getMessage().isBlank());
    }
}