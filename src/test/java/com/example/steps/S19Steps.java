package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId = "session-123";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is initialized in constructor
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "ACCOUNTS_MENU", "OPEN");
        executeCommand(cmd);
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertEquals(MenuNavigatedEvent.class, resultEvents.get(0).getClass());
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Do not mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        aggregate.expireSession();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        // Setup valid state otherwise
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We check for IllegalStateException or IllegalArgumentException which indicates domain constraint violation
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    private void executeCommand(NavigateMenuCmd cmd) {
        try {
            // Specific context for the context violation scenario
            if (cmd.menuId().equals("INVALID_CONTEXT") || (cmd instanceof NavigateMenuCmd && "INVALID_CONTEXT".equals(((NavigateMenuCmd) cmd).menuId()))) {
                 // Logic handled inside aggregate via specific menuId
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Special hook for the context violation test since we need to inject the bad state via command
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_NavigateMenuCmd_command_is_executed_with_invalid_context() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "INVALID_CONTEXT", "OPEN");
        executeCommand(cmd);
    }
}