package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private String sessionId;
    private String menuId;
    private String action;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = "ts-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid state for positive case
        aggregate.clearEvents(); // Clean up initialization events if any
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId initialized in "a valid TellerSession aggregate"
        assertNotNull(sessionId);
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
            aggregate.execute(new NavigateMenuCmd(sessionId, menuId, action));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Command should not throw exception");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof MenuNavigatedEvent);
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = "ts-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = "ts-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Valid auth
        aggregate.markAsInactive(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        sessionId = "ts-bad-ctx";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Valid auth
        aggregate.markContextInvalid(); // Force invalid context state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}