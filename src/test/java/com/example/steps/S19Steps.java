package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.MenuNavigatedEvent;
import com.example.domain.uimodel.model.NavigateMenuCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd on TellerSession.
 */
public class S19Steps {

    // System Under Test
    private TellerSessionAggregate aggregate;
    
    // Command Execution Artifacts
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test Data Builders
    private static final String VALID_SESSION_ID = "SESSION-001";
    private static final String VALID_TELLER_ID = "TELLER-Alice";
    private static final String INITIAL_MENU = "MAIN_MENU";
    private static final String TARGET_MENU = "DEPOSIT_MENU";
    private static final String VALID_ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Setup a valid state manually (simulating a hydrated aggregate)
        aggregate.setState(
            VALID_TELLER_ID, 
            true, 
            INITIAL_MENU, 
            Instant.now()
        );
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by constant VALID_SESSION_ID in command construction
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled by constant TARGET_MENU in command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled by constant VALID_ACTION in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(
            VALID_SESSION_ID,
            TARGET_MENU,
            VALID_ACTION,
            aggregate.getCurrentMenuId() // Current context must match aggregate state
        );
        executeCommand(cmd);
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(VALID_SESSION_ID, navEvent.aggregateId());
        assertEquals(TARGET_MENU, navEvent.targetMenuId());
    }

    // ---------------------------------------------------------------------
    // Negative Scenarios
    // ---------------------------------------------------------------------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setState(
            VALID_TELLER_ID, 
            false, // Not authenticated
            INITIAL_MENU, 
            Instant.now()
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setState(
            VALID_TELLER_ID, 
            true, 
            INITIAL_MENU, 
            Instant.now().minusSeconds(3600) // 1 hour ago (Timeout is 15 mins)
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setState(
            VALID_TELLER_ID, 
            true, 
            INITIAL_MENU, 
            Instant.now()
        );
    }

    // Override standard execution for context violation to inject wrong context ID
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_NavigateMenuCmd_command_is_executed_with_invalid_context() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(
            VALID_SESSION_ID,
            TARGET_MENU,
            VALID_ACTION,
            "WRONG_CONTEXT" // Explicitly violating the invariant
        );
        executeCommand(cmd);
    }

    // Catch-all for the generic 'NavigateMenuCmd command is executed' in negative scenarios
    // We need to ensure we call the right when based on the setup. 
    // However, Cucumber matches method by text. The text in feature is identical.
    // So this method handles the generic call for all 3 negative scenarios.
    // The 'Context' scenario is tricky because the cmd construction depends on the violation.
    // We will inspect the aggregate state to decide what cmd to build.
    
    // NOTE: In real-world, we might parameterize the When. Given constraints, we assume
    // the step definitions are matched by Cucumber regex. The feature text for all 3 is:
    // "When the NavigateMenuCmd command is executed"
    
    // To support the Context violation specifically without changing the feature file text,
    // we check if we are in that specific state. This is a pragmatic compromise for the generator.
    
    // We actually need separate @When methods or logic. Since text is identical, we use one method.
    @Override // Overriding the generic method above implicitly by name matching
    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_generic() {
        // Detect which violation scenario we are in based on aggregate state
        if (!aggregate.isAuthenticated()) {
             // Auth violation: command doesn't matter much, but let's make it valid otherwise
             NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, TARGET_MENU, VALID_ACTION, aggregate.getCurrentMenuId());
             executeCommand(cmd);
        } else if (aggregate.getCurrentMenuId().equals(INITIAL_MENU)) {
             // Likely the Context violation (others are timeout or auth)
             // If we are here and Auth is true, and we aren't timed out (logic assumption), 
             // we assume the context violation case needs the wrong context.
             // However, distinguishing purely from state is hard.
             // Let's rely on the fact that the Context setup sets state to INITIAL_MENU.
             // We will send a command claiming we are at "OTHER_MENU".
             NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, TARGET_MENU, VALID_ACTION, "OTHER_MENU");
             executeCommand(cmd);
        } else {
             // Default or Timeout (Timeout is handled by state time, command can be valid)
             NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, TARGET_MENU, VALID_ACTION, aggregate.getCurrentMenuId());
             executeCommand(cmd);
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify message content to ensure it's a domain error, not a system crash
        String msg = capturedException.getMessage();
        assertNotNull(msg);
        assertTrue(
            msg.contains("authenticated") || msg.contains("timeout") || msg.contains("context"),
            "Error message should match the invariant violation: " + msg
        );
    }

    // ---------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
