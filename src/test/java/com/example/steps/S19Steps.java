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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId;
    private String menuId;
    private String action;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId, Duration.of(15, ChronoUnit.MINUTES));
        aggregate.markAuthenticated(); // Ensure valid for happy path
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId already initialized in 'a_valid_teller_session_aggregate'
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        menuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
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
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    // Scenario: Authentication Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId, Duration.ofMinutes(15));
        // Note: markAuthenticated() is NOT called
        menuId = "DASHBOARD";
        action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException based on our implementation
        assertTrue(capturedException instanceof IllegalStateException); 
    }

    // Scenario: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId, Duration.ofMinutes(15));
        aggregate.markAuthenticated();
        
        // Set last activity to 20 minutes ago (beyond 15 min timeout)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
        
        menuId = "MAIN_MENU";
        action = "ENTER";
    }

    // Scenario: Invalid Operational Context
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = "session-locked";
        aggregate = new TellerSessionAggregate(sessionId, Duration.ofMinutes(15));
        aggregate.markAuthenticated();
        
        // Set operational context to LOCKED, preventing navigation
        aggregate.setOperationalContext("LOCKED");
        
        menuId = "MAIN_MENU";
        action = "ENTER";
    }
}
