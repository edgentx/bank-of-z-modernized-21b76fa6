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

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123")
                .markAuthenticated("teller-456")
                .withCurrentScreen("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123")
                .markUnauthenticated()
                .withCurrentScreen("LOGIN_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123")
                .markAuthenticated("teller-456")
                .markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-123")
                .markAuthenticated("teller-456")
                .withCurrentScreen("ACCOUNT_DETAILS");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in command construction
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Construct command based on aggregate state for context checks
            String currentContext = aggregate.getCurrentScreenId();
            // For the violation scenario where context must mismatch:
            // if we are in ACCOUNT_DETAILS, let's pretend the UI thinks it's in MAIN_MENU
            if ("ACCOUNT_DETAILS".equals(aggregate.getCurrentScreenId())) {
                currentContext = "MAIN_MENU";
            }

            command = new NavigateMenuCmd(
                aggregate.id(),
                "TARGET_MENU",
                "ENTER",
                currentContext
            );
            
            resultingEvents = aggregate.execute(command);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultingEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("TARGET_MENU", event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Verify it's a domain logic error (IllegalStateException) and not a system NPE
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
