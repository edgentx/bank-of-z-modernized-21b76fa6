package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        // Seed to a valid state
        aggregate.apply(new SessionStartedEvent(id, "teller-1", "terminal-1", Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        String id = "session-not-auth";
        aggregate = new TellerSessionAggregate(id);
        // Not starting session implies not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_timed_out() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        // Start session but time it out
        aggregate.apply(new SessionStartedEvent(id, "teller-1", "terminal-1", Instant.now().minusSeconds(3600)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_bad_context() {
        String id = "session-bad-ctx";
        aggregate = new TellerSessionAggregate(id);
        aggregate.apply(new SessionStartedEvent(id, "teller-1", "terminal-1", Instant.now()));
        // Trying to navigate to a menu that is inaccessible in the current context
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the aggregate initialization
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Context for the command
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context for the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // For the successful case, valid inputs
            if (aggregate.id().equals("session-123")) {
                Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "SELECT");
                resultEvents = aggregate.execute(cmd);
            }
            // For the bad context case, we simulate an invalid transition
            else if (aggregate.id().equals("session-bad-ctx")) {
                Command cmd = new NavigateMenuCmd(aggregate.id(), "ADMIN_MENU", "ENTER");
                resultEvents = aggregate.execute(cmd);
            }
            else {
                // For unauthenticated or timeout, the command itself doesn't matter as much, but we need valid structure
                Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "SELECT");
                resultEvents = aggregate.execute(cmd);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected exception to be thrown");
        // Verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
