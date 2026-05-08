package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // By default, assume valid authenticated state for the happy path setup
        this.aggregate.markAuthenticated("teller-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is already set in the aggregate setup
        assertNotNull(this.aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultingEvents = this.aggregate.execute(cmd);
            this.thrownException = null;
        } catch (Exception e) {
            this.thrownException = e;
            this.resultingEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(this.resultingEvents, "Events list should not be null");
        assertEquals(1, this.resultingEvents.size(), "Exactly one event should be emitted");
        assertTrue(this.resultingEvents.get(0) instanceof MenuNavigatedEvent, "Event type should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) this.resultingEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(this.menuId, event.targetMenuId());
        assertEquals(this.action, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.setAuthenticated(false); // Ensure not authenticated
        this.menuId = "ADMIN_MENU";
        this.action = "VIEW";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated("teller-02");
        this.aggregate.markExpired(); // Force timeout
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        this.sessionId = "session-bad-ctx";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated("teller-03");
        // We use the specific "INVALID_CONTEXT" trigger defined in the aggregate
        this.menuId = "INVALID_CONTEXT";
        this.action = "GOTO";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(this.thrownException, "Expected an exception to be thrown, but none was");
        // In DDD, command rejections are usually modeled as Exceptions or specific Result objects.
        // Here we validate that an IllegalStateException (domain error) was thrown.
        assertTrue(this.thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Optionally verify the message matches the invariant description
        assertTrue(this.thrownException.getMessage() != null && !this.thrownException.getMessage().isBlank());
    }
}