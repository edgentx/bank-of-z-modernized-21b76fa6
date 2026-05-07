package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "sess-123";
        String tellerId = "teller-001";
        Instant now = Instant.now();
        
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate initialization via internal mutation logic typically done in a constructor or apply method
        // Since we are unit testing the aggregate behavior directly, we hydrate it to a valid state.
        aggregate.forceStateForTest(tellerId, true, "MAIN_MENU", now, Duration.ofMinutes(15));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The ID is set in the previous step. Implicitly satisfied.
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Will be part of the command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be part of the command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNT_INQUIRY", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        String sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.forceStateForTest(null, false, "LOGIN", Instant.now(), Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Set last active time to 20 minutes ago (timeout is usually 15)
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.forceStateForTest("teller-001", true, "MAIN_MENU", past, Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_state() {
        String sessionId = "sess-badstate";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate being in a state where navigation is blocked (e.g. LOCKED or TRANSACTION_IN_PROGRESS)
        aggregate.forceStateForTest("teller-001", true, "LOCKED_STATE", Instant.now(), Duration.ofMinutes(15));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // We accept IllegalStateException or IllegalArgumentException as domain errors in this model
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException), but got: " + thrownException.getClass().getSimpleName()
        );
    }
}
