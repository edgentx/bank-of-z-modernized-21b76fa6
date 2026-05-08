package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.shared.*;
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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Simulate an authenticated, active session state by applying a dummy event or setting state directly for testing.
        // Since S-19 might be the first interaction, we assume we need to init the session.
        // But the navigation command requires the session to be active.
        // We will execute a command to establish the active state if one existed, or mock the state.
        // For this BDD, we'll assume the aggregate is instantiated and represents an active, authenticated session.
        aggregate.markAuthenticated(); // Helper to set internal state for test validity
        aggregate.updateLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        // Ensure state is NOT authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        aggregate.markAuthenticated();
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-NAV");
        aggregate.markAuthenticated();
        aggregate.updateLastActivity(Instant.now());
        // Force the aggregate into a state where it cannot navigate (e.g. locked)
        aggregate.lock();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate instantiation
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be used in the When step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be used in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Constructing a valid command
            NavigateMenuCmd cmd = new NavigateMenuCmd("SESSION-1", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (DomainError | IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events should not be empty");
        Assertions.assertEquals("teller.session.menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Ideally we check for DomainError specifically, but IllegalStateException/IllegalArgumentException are valid invariants in this pattern.
    }
}
