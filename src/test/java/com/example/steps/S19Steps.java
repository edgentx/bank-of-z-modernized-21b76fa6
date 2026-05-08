package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
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
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Bootstrap the aggregate to a valid active state
        aggregate.bootstrap(new TellerSessionOpenedEvent("session-123", "teller-456", Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-invalid-auth");
        // Aggregate is in default state (null user) or opened with null user
        // relying on internal state validation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Bootstrap with an old timestamp to simulate timeout
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(30)); // assuming timeout < 30 mins
        aggregate.bootstrap(new TellerSessionOpenedEvent("session-timeout", "teller-456", oldTime));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.bootstrap(new TellerSessionOpenedEvent("session-bad-state", "teller-456", Instant.now()));
        // Force invalid state via a non-public transition or just leave it hanging if the model prevents dual states
        // Since the aggregate is stateless other than the open event, we check valid transitions.
        // To test rejection, we might need a state machine guard. 
        // For this aggregate, if it's already at 'EXIT', it can't navigate.
        aggregate.apply(new MenuNavigatedEvent("session-bad-state", "EXIT", "LOGOUT", Instant.now()));
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate initialization
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("MAIN_MENU", "ENTER");
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertEquals("menu.navigated", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check it's an IllegalStateException or similar domain logic violation
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof UnknownCommandException || // If validation fails before dispatch
            caughtException instanceof IllegalArgumentException
        );
    }
}
