package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("sess-123");
        aggregate.markAuthenticated(); // Valid implies authenticated
        aggregate.setCurrentMenu("MAIN_MENU"); // Valid context
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is handled in aggregate constructor, we bind it in the command later
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in When step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("sess-401");
        // Intentionally NOT calling markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("sess-408");
        aggregate.markAuthenticated();
        aggregate.markInactive(); // Force timeout condition
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("sess-400");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("CURRENT_MENU");
        // We will try to navigate to the same menu or null to trigger error
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Defaults for "valid" if context implies success, else specific violation setup
            String targetMenu = "DEPOSIT_SCREEN";
            String action = "ENTER";

            // Adjust for specific violation case if needed (Context check)
            if (aggregate.getCurrentMenu() != null && aggregate.getCurrentMenu().equals("CURRENT_MENU")) {
                 targetMenu = "CURRENT_MENU"; // Trigger "Already at menu" error
            }

            command = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent evt = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", evt.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // We expect IllegalStateException for invariant violations in this pattern
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}