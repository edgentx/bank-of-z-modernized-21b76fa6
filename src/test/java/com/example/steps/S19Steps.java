package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private static final String SESSION_ID = "session-123";
    private static final String MENU_ID = "MAIN_MENU";
    private static final String ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated("teller-001");
        aggregate.setLastActivity(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by constructor
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Implicitly handled in Command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Implicitly handled in Command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        Command cmd = new NavigateMenuCmd(SESSION_ID, MENU_ID, ACTION);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals(MENU_ID, event.targetMenuId());
        Assertions.assertEquals(ACTION, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Do not authenticate
        aggregate.setLastActivity(Instant.now());
        // Enforce invariants
        aggregate.setEnforceAuth(true);
        aggregate.setEnforceTimeout(false); // Avoid timeout interference
        aggregate.setEnforceNavState(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated("teller-001");
        // Set activity to 20 minutes ago (default timeout is 15)
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.setEnforceAuth(false);
        aggregate.setEnforceTimeout(true);
        aggregate.setEnforceNavState(false);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated("teller-001");
        aggregate.setLastActivity(Instant.now());
        aggregate.setEnforceAuth(false);
        aggregate.setEnforceTimeout(false);
        aggregate.setEnforceNavState(true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected exception to be thrown");
        // We accept IllegalStateException or IllegalArgumentException as domain errors
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}