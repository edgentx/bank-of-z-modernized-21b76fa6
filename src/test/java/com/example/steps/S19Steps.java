package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Create a fresh, authenticated, valid session
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.hydrate(new TellerSessionAggregate.SessionState(
                "session-123",
                "teller-1",
                true,          // authenticated
                "MAIN_MENU",   // current screen
                Instant.now().minusSeconds(10) // last active (not timed out)
        ));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.hydrate(new TellerSessionAggregate.SessionState(
                "session-401",
                "teller-unknown",
                false, // NOT authenticated
                "LOGIN_SCREEN",
                Instant.now()
        ));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        // Last active 15 minutes ago (assuming 10 min timeout)
        aggregate.hydrate(new TellerSessionAggregate.SessionState(
                "session-408",
                "teller-1",
                true,
                "MAIN_MENU",
                Instant.now().minus(Duration.ofMinutes(15))
        ));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-409");
        // State is corrupt or null
        aggregate.hydrate(new TellerSessionAggregate.SessionState(
                "session-409",
                "teller-1",
                true,
                null, // Invalid context
                Instant.now()
        ));
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // ID is implied by the aggregate construction
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When block construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When block construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "ACCOUNT_DETAILS", "ENTER");
        // Adjust the command ID if the aggregate ID is different in negative tests
        if(aggregate != null) {
             cmd = new NavigateMenuCmd(aggregate.id(), "NEXT_MENU", "F3");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // In DDD, invariants are enforced by Exceptions (usually IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
                caughtException instanceof IllegalStateException || 
                caughtException instanceof IllegalArgumentException
        );
    }
}
