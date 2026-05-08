package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private static final String TEST_SESSION_ID = "session-123";
    private static final Duration TIMEOUT = Duration.ofMinutes(30);

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, TIMEOUT);
        aggregate.markAuthenticated(); // Assume authenticated for base case
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, TIMEOUT);
        // Explicitly not authenticated
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, TIMEOUT);
        aggregate.markAuthenticated();
        aggregate.markSessionExpired(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID, TIMEOUT);
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("USER_HOME"); // Context: standard user
        // Trying to navigate to SYSTEM_ADMIN from USER_HOME will trigger the invariant check
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by constant in aggregate construction
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in 'When' step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in 'When' step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // If we are in the 'Navigation state' violation scenario, we target the restricted menu
            String targetMenu = (aggregate.isAuthenticated() 
                                && "USER_HOME".equals(aggregate.getCurrentMenuId()) 
                                && !aggregate.getCurrentMenuId().isEmpty()) 
                                ? "SYSTEM_ADMIN" 
                                : "DEPOSITS";

            NavigateMenuCmd cmd = new NavigateMenuCmd(TEST_SESSION_ID, targetMenu, "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        // Check message matches one of the invariant error messages
        String msg = thrownException.getMessage();
        assertTrue(msg.contains("authenticated") || 
                   msg.contains("timeout") || 
                   msg.contains("context"));
    }
}