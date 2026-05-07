package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermemory.model.NavigateMenuCmd;
import com.example.domain.tellermemory.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.authenticate("teller-001"); // Ensure valid state
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-404");
        // Intentionally do not authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.authenticate("teller-001");
        // Simulate timeout by forcing the last activity time into the past
        aggregate.forceLastActivityTime(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.authenticate("teller-001");
        aggregate.forceNavigationState("INVALID_CONTEXT");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate construction in Given steps
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            if (aggregate.id().equals("session-404")) cmd = new NavigateMenuCmd("session-404", "MAIN_MENU", "ENTER");
            if (aggregate.id().equals("session-timeout")) cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER");
            if (aggregate.id().equals("session-bad-state")) cmd = new NavigateMenuCmd("session-bad-state", "MAIN_MENU", "ENTER");
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Checking if it's an illegal state or argument exception (Domain Error)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException || capturedException instanceof UnknownCommandException);
    }
}
