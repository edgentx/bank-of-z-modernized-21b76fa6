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
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class S19Steps {
    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private static final String SESSION_ID = "session-123";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(); // Ensure authenticated for positive path
        aggregate.setCurrentMenuId("INIT_SCREEN"); // Ensure distinct menu
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markUnauthenticated(); // Violation: not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        // Set activity to 20 minutes ago (Default timeout is 15m)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        // Set the current menu to the one we will try to navigate to (simulating invalid state transition)
        aggregate.setCurrentMenuId("TARGET_MENU");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by constant in constructor, assume valid unless specific invalid scenario added
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Implicitly handled in When step via cmd construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Implicitly handled in When step via cmd construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        // Construct command. 
        // Note: For the 'Navigation state' violation scenario, we use the same ID as the current menu
        String targetMenu = "TARGET_MENU";
        
        // If we are in the generic valid scenario, ensure we aren't at the target menu yet
        if(aggregate != null && "TARGET_MENU".equals(aggregate.getCurrentMenuId())) {
             // This is the violation scenario logic effectively covered by the Given, 
             // but we ensure the command targets the problematic state here if needed.
             // The violation setup sets current to TARGET_MENU.
        } else if (aggregate != null && "INIT_SCREEN".equals(aggregate.getCurrentMenuId())) {
            // Standard happy path
        }

        cmd = new NavigateMenuCmd(SESSION_ID, targetMenu, "ENTER");
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("TARGET_MENU", event.menuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // We expect IllegalStateException based on our invariants
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}