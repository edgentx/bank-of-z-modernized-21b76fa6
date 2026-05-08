package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private Aggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate an authenticated, active session state
        // We must mutate state directly to satisfy the preconditions of the aggregate
        // since we don't have an 'Initiate' command defined in this story scope.
        ((TellerSessionAggregate) aggregate).hydrateForTest(
            "user-123",                // authenticated teller
            Instant.now().minusSeconds(60), // last activity 60s ago
            "MENU_MAIN",               // current context
            false                       // not timed out
        );
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly part of the aggregate/command structure, 
        // usually bound to the aggregate instance.
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Defined in command construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Defined in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Construct command with valid inputs matching the scenario
            command = new NavigateMenuCmd("session-123", "MENU_ACCT_HISTORY", "ENTER");
            resultEvents = aggregate.execute(command);
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
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Create a session where the teller ID is missing/null (unauthenticated)
        ((TellerSessionAggregate) aggregate).hydrateForTest(
            null, // No authenticated user
            Instant.now().minusSeconds(10),
            "MENU_LOGIN",
            false
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Create a session where last activity was 30 minutes ago (assuming timeout is 15m)
        ((TellerSessionAggregate) aggregate).hydrateForTest(
            "user-123",
            Instant.now().minus(Duration.ofMinutes(30)),
            "MENU_MAIN",
            false
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        // Current state does not match the expected transition (e.g., closed session)
        // We simulate this by setting a specific flag or context that blocks navigation
        ((TellerSessionAggregate) aggregate).hydrateForTest(
            "user-123",
            Instant.now().minusSeconds(5),
            "SCREEN_CLOSED", // Terminal closed context
            false
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Verify it's a domain error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected a domain rule violation exception"
        );
    }
}
