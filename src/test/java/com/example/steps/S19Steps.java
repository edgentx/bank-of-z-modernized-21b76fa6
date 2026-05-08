package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Ensure base valid state
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // 'authenticated' defaults to false in constructor, so we are good.
        // We assume the user tried to initiate but failed auth, or session dropped.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markExpired(); // Manually set time to past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("MAIN_MENU"); // Set context where 'BACK' is invalid
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by aggregate ID, but we ensure command matches
        // No-op, value fixed in When step
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // No-op, value fixed in When step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // No-op, value fixed in When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Construct command with generally valid inputs
            // Specific invalid contexts are set up in the aggregate state above
            String targetMenu = "DEPOSIT_SCREEN";
            String action = "ENTER";

            // If we are testing the "Navigation state... context" failure for 'BACK'
            if ("MAIN_MENU".equals(aggregate.getCurrentMenuId())) {
                 // Triggering the specific logic defined in Aggregate for invalid context
                 action = "BACK"; 
            }

            command = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We verify it's an unchecked exception (IllegalStateException or IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    // Reset state between scenarios if necessary (Cucumber creates new instance by default)
}
