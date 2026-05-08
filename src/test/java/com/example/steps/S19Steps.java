package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellerauth.model.NavigateMenuCmd;
import com.example.domain.tellerauth.model.MenuNavigatedEvent;
import com.example.domain.tellerauth.model.TellerSessionAggregate;
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
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123", "teller-456");
        aggregate.authenticate("teller-456", "pass", Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123", "teller-456");
        // Intentionally not calling authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123", "teller-456");
        Duration timeout = Duration.ofMinutes(30);
        // Authenticate 31 minutes ago
        aggregate.authenticate("teller-456", "pass", Instant.now().minus(timeout).minusSeconds(60));
        aggregate.setTimeoutDuration(timeout);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123", "teller-456");
        aggregate.authenticate("teller-456", "pass", Instant.now());
        // Put aggregate in a CLOSED state where navigation is invalid
        aggregate.terminate(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate construction in given steps
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Placeholder: In real test might store this in a context object
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Placeholder: In real test might store this in a context object
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected exception but command succeeded");
        // Domain errors are typically IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException/IllegalArgumentException), got: " + caughtException.getClass().getSimpleName()
        );
    }
}
