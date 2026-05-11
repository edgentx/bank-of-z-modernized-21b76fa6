package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-42");
        aggregate.setCurrentMenu("MAIN_MENU");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.setCurrentMenu("MAIN_MENU");
        // Intentionally NOT authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-42");
        aggregate.setCurrentMenu("MAIN_MENU");
        // Set last activity to 20 minutes ago
        aggregate.setLastActivityAt(Instant.now().minus(java.time.Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        aggregate.markAuthenticated("teller-42");
        aggregate.setCurrentMenu("SETTINGS_MENU"); // Aggregate thinks we are here
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in cmd construction below or context setup
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in cmd construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in cmd construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        // Default valid cmd data, overrides possible in specific violation steps if needed
        // But typically the violation is in the Aggregate state, not the cmd data
        String expectedCurrent = aggregate.getCurrentMenu();
        
        // If aggregate has no menu set (clean), set one to allow command construction logic consistency
        if (expectedCurrent == null) expectedCurrent = "MAIN_MENU";

        cmd = new NavigateMenuCmd(aggregate.id(), expectedCurrent, "NEXT_MENU", "ENTER");

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent evt = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", evt.type());
        Assertions.assertEquals("NEXT_MENU", evt.nextMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Check it's not our UnknownCommandException
        Assertions.assertFalse(thrownException.getMessage().contains("Unknown command"));
    }
}