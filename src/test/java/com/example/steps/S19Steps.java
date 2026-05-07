package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true);
        // Ensure activity is recent
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentContext("ROOT");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly "session-123" from the aggregate creation
        // In a real app, we might load from a repository here.
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Valid ID used in the When step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Valid action used in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.setAuthenticated(false); // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.setAuthenticated(true);
        // Violation: Set last activity to 20 minutes ago (timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-409");
        aggregate.setAuthenticated(true);
        // Violation: Context is LOCKED, preventing navigation
        aggregate.setCurrentContext("LOCKED");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // We expect IllegalStateException as per our Domain Logic implementation
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
