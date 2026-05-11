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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.hydrate(
            "session-1",
            "teller-123",
            true,           // authenticated
            Instant.now(),  // lastActivity (now)
            "MAIN_MENU"     // currentMenu
        );
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate ID setup in previous step
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
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-1", "DEPOSIT_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("DEPOSIT_MENU", event.targetMenu());
        assertEquals("session-1", event.aggregateId());
    }

    // Scenario 2: Auth Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        aggregate.hydrate(
            "session-bad-auth",
            "teller-unknown",
            false, // NOT authenticated
            Instant.now(),
            "LOGIN_SCREEN"
        );
    }

    // Scenario 3: Timeout Failure
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate activity 30 minutes ago (assuming timeout is 15m)
        Instant pastActivity = Instant.now().minus(Duration.ofMinutes(30));
        aggregate.hydrate(
            "session-timeout",
            "teller-123",
            true,
            pastActivity,
            "MAIN_MENU"
        );
    }

    // Scenario 4: Navigation State Failure
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.hydrate(
            "session-bad-state",
            "teller-123",
            true,
            Instant.now(),
            "LOCKED_SCREEN" // Invalid context for standard navigation
        );
    }

    // Common Negative Outcome
    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNull(resultEvents);
        assertNotNull(thrownException);
        // Verify it's a standard exception used for domain errors (IllegalStateException or IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

}
