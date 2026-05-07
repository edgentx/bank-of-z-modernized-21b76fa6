package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test Constants
    private static final String TEST_SESSION_ID = "SESSION-101";
    private static final String TEST_TELLER_ID = "TELLER-ALICE";
    private static final String TEST_MENU_ID = "MENU_ACCOUNT_INQUIRY";
    private static final String TEST_ACTION = "SELECT";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        // Setup valid state: authenticated and active
        aggregate.markAuthenticated(TEST_TELLER_ID);
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by constant in command creation
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled by constant in command creation
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled by constant in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(TEST_SESSION_ID, TEST_MENU_ID, TEST_ACTION);
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(TEST_MENU_ID, event.targetMenuId());
        assertEquals(TEST_TELLER_ID, event.tellerId());
        assertEquals("menu.navigated", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        // Intentionally not calling markAuthenticated()
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markAuthenticated(TEST_TELLER_ID);
        // Force expiry
        aggregate.expireSession(); 
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markAuthenticated(TEST_TELLER_ID);
        // Set a context that disallows the navigation in the command logic
        aggregate.setCurrentContext("TRANS_ROOT");
        repo.save(aggregate);
        // We will try to navigate to MENU_ADMIN in the 'When' step via a modification or separate logic,
        // but since the 'When' step is shared, we rely on the aggregate state to reject the default valid command 
        // OR we can modify the command execution context. 
        // However, Cucumber steps are usually specific. Let's adjust the command in a custom When or modify the flow.
        // For simplicity in this pattern, we assume the setup puts it in a state where ANY navigation fails or specific navigation fails.
        // The specific logic in the aggregate rejects ADMIN from TRANS_ROOT. 
        // We need to inject the specific command into the execution context for this scenario.
    }

    // Overriding the When for the context violation scenario (Step Implementation variation)
    @When("the NavigateMenuCmd command is executed on invalid context")
    public void the_NavigateMenuCmd_command_is_executed_invalid_context() {
        try {
            // Attempting the forbidden navigation
            NavigateMenuCmd cmd = new NavigateMenuCmd(TEST_SESSION_ID, "MENU_ADMIN", "GOTO");
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for domain error types (IllegalStateException or IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
