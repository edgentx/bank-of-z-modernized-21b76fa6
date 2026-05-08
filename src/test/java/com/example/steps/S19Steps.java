package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.commands.NavigateMenuCmd;
import com.example.domain.tellersession.model.events.MenuNavigatedEvent;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private String sessionId = "session-123";
    private String menuId = "MENU_MAIN";
    private String action = "DISPLAY";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state (authenticated)
        aggregate.markAuthenticated("teller-001");
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // DO NOT authenticate -> violates invariant
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        // Force timeout state
        aggregate.markExpired();
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        repo.save(aggregate);
        // We will trigger the violation by providing bad command data (nulls) in the When step
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId = "session-123"; // Already initialized
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // menuId = "MENU_MAIN"; // Already initialized
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // action = "DISPLAY"; // Already initialized
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // If this is the context violation scenario, force bad data
            if (aggregate.getCurrentMenuId() == null && aggregate.getCurrentAction() == null && aggregate.id().equals("session-123")) {
                // Check scenario state implicitly or assume standard valid data unless overridden
                // For the specific "Navigation state..." scenario, we need to send bad data.
                // However, Cucumber scenarios are isolated. 
                // To keep it simple, we assume valid data here, and handle the specific violation
                // in a specific @When step or by checking the aggregate state.
                // Let's rely on the standard defaults, but if the aggregate wasn't authenticated (previous scenario), it fails there.
                // For the "context" violation, let's assume valid inputs here.
            }
            
            // Check if this is the 'context' violation scenario by inspecting the aggregate state or a flag.
            // Actually, it's cleaner to assume valid inputs unless the test explicitly overrides them.
            // But the Gherkin says "Given... violates: Navigation state...".
            // This implies the AGGREGATE state is the issue, OR the inputs are invalid. 
            // The prompt says "Navigation state must accurately reflect...".
            // Let's assume standard valid inputs for the generic When.
            // If specific inputs were needed for the negative case, the Gherkin would say "And an invalid menuId is provided".
            // Since it doesn't, the "Given" setup handles the precondition (like expired session or unauthenticated).
            // BUT for "Navigation state...", the precondition is abstract. Let's assume standard inputs.
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
    
    // Overriding When for the specific violation case to make it robust
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_NavigateMenuCmd_command_is_executed_with_invalid_context() {
        try {
             // Force invalid inputs to satisfy the "context" violation logic within the aggregate
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, null, null);
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
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
        assertNull(caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // The implementation throws IllegalStateException or IllegalArgumentException, both are RuntimeException/Errors in domain context
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        assertNull(resultEvents); // No events should be emitted if command rejected
    }
}
