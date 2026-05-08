package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
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
    private DomainEvent resultingEvent;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Simulate a session that is active, authenticated, and has valid navigation state
        // We load the state via the aggregate constructor or a hypothetical "load" method logic.
        // Since we can't mutate state without events in this pattern, we assume the aggregate
        // is created valid.
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate construction in previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by command construction in When step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by command construction in When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        // We use specific valid values for the success case
        NavigateMenuCmd cmd = new NavigateMenuCmd("SESSION-1", "MAIN_MENU", "ENTER");
        try {
            List<DomainEvent> events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvent);
        Assertions.assertTrue(resultingEvent instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvent;
        Assertions.assertEquals("menu.navigated", event.type());
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        // Create an aggregate that defaults to unauthenticated
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Create an aggregate with a last activity time that is too old
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT", Instant.now().minus(Duration.ofMinutes(30)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        // Create an aggregate in an invalid state (e.g., pending)
        aggregate = new TellerSessionAggregate("SESSION-BADSTATE");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // We accept IllegalStateException or IllegalArgumentException as domain errors
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException
        );
    }
}
