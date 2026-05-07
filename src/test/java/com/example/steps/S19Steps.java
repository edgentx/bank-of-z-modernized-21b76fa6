package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions for S-19: Teller Session Navigation.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state for success scenario
        aggregate.markAuthenticated("teller-123");
        aggregate.setCurrentMenu("MAIN_MENU"); // Valid context
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Deliberately do NOT call markAuthenticated.
        // Check internal state to ensure violation exists
        assertFalse(aggregate.isAuthenticated(), "Teller should not be authenticated for this test");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_inactivity() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-123"); // Must be auth to fail on timeout next
        
        // Force last activity to 16 minutes ago (Threshold is 15m)
        aggregate.forceLastActivity(Instant.now().minus(Duration.ofMinutes(16)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        String sessionId = "session-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-123");
        
        // Set current context to BALANCE_INQUIRY
        aggregate.setCurrentMenu("BALANCE_INQUIRY");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Used by the positive scenario to construct the command.
        // Assuming we use the aggregate's ID for the command.
        if (aggregate == null) {
            throw new RuntimeException("Aggregate must be created first");
        }
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Placeholder; actual value used in When step.
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Placeholder; actual value used in When step.
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Construct command. For violation scenarios, we try to navigate to specific menus 
            // to trigger the logic defined in the aggregate.
            String targetMenu = "ACCOUNT_SUMMARY";
            String action = "ENTER";
            
            // Exception: For the context violation, we try to go to the SAME menu
            // to trigger the "Already on menu" error.
            if (aggregate.getCurrentMenuId() != null && aggregate.getCurrentMenuId().equals("BALANCE_INQUIRY")) {
                targetMenu = "BALANCE_INQUIRY";
                action = "ENTER"; 
            }

            command = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(aggregate.id(), navEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // In our domain, we use IllegalStateException or IllegalArgumentException for domain rule violations.
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Exception should be a domain rule violation (IllegalStateException or IllegalArgumentException)"
        );
    }
}