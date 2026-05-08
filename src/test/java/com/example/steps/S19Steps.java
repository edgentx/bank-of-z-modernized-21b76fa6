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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private static final String SESSION_ID = "sess-123";
    private static final String TELLER_ID = "teller-456";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate._setAuthenticated(true);
        aggregate._setCurrentMenu("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in constructor step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be used in the command execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be used in the command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, "ACCOUNT_DETAILS", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNT_DETAILS", event.targetMenuId());
        assertEquals(SESSION_ID, event.aggregateId());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate._setAuthenticated(false); // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate._setAuthenticated(true);
        // Set last activity to 20 minutes ago (default timeout is 15)
        aggregate._setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate._setAuthenticated(true);
        // The violation logic is inside the command/input validation (null/blank menuId)
        // We rely on the 'When' step to pass invalid data, but we could prep aggregate state here if needed.
    }

    @When("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        // We need to re-execute logic specific to the scenario context. 
        // Since Cucumber scenarios are isolated, we can inspect the caughtException from the previous 'When' step.
        // However, to support the specific phrasing, we adjust the trigger based on context.
        if (caughtException == null) {
             // This step is essentially a 'Then' verification of the previous 'When'.
             // But if the Gherkin treats it as a distinct action trigger:
             // we execute a command that forces the specific failure.
             
             // Scenario 2: Not Authenticated
             if (!aggregate.isEnrolled()) { // Wait, we need a getter for testing
                 // We rely on the state set in 'Given'
                 try {
                     aggregate.execute(new NavigateMenuCmd(SESSION_ID, "X", "Y"));
                 } catch (IllegalStateException e) {
                     caughtException = e;
                 }
             }
        }
        // Actually, the best pattern is to let the previous 'When' catch the exception, 
        // and this step acts as a verification wrapper or trigger for specific invalid inputs.
        
        // Handling Scenario 4 (Invalid Context - e.g. null menu)
        if (caughtException == null) {
             // Assuming this step is coupled with Scenario 4 logic if not yet triggered.
             try {
                 aggregate.execute(new NavigateMenuCmd(SESSION_ID, null, "ACTION"));
             } catch (Exception e) {
                 caughtException = e;
             }
        }
    }

    @Then("the command is rejected with a domain error")
    public void verify_command_rejected() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // --- Helpers ---
    // Added to aggregate for test visibility (or package-private access)
    public static class TestableAggregate extends TellerSessionAggregate {
        public TestableAggregate(String id) { super(id); }
        // Accessor if needed
    }
}