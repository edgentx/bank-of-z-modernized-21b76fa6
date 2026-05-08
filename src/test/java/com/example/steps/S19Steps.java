package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate a prior event to bring aggregate to valid state (authenticated)
        aggregate.bootstrap(new TellerAuthenticatedEvent("session-123", "teller-1", Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization in previous step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be passed in the command
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be passed in the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Default valid command data for the success scenario
            Command cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "OPEN_ACCOUNT");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Aggregate is created but no authentication event applied, so isAuthenticated is false
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Bootstrap as authenticated
        aggregate.bootstrap(new TellerAuthenticatedEvent("session-timeout", "teller-1", Instant.now().minus(Duration.ofHours(2))));
        // Force last activity to be old
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.bootstrap(new TellerAuthenticatedEvent("session-bad-nav", "teller-1", Instant.now()));
        // Simulate a bad state: navigating to a screen that requires a context not present (e.g. Transaction Screen without Account context)
        // In this model, we pretend 'TRANS_DETAILS' is invalid if currentMenu is 'MAIN' (simplified invariant logic for the test)
        aggregate.setCurrentMenu("INVALID_CONTEXT");
    }
}
