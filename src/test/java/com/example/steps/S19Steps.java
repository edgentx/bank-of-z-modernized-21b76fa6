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

/**
 * Cucumber Steps for S-19: NavigateMenuCmd.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set up a valid, authenticated state
        this.aggregate.applyState("TELLER-01", true, Instant.now(), TellerSessionAggregate.ScreenState.MAIN_MENU);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.sessionId = "SESSION-123";
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "ACCT_INQUIRY";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "SESSION-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly set authenticated to false
        this.aggregate.applyState("TELLER-01", false, Instant.now(), TellerSessionAggregate.ScreenState.AUTH);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "SESSION-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set last activity to 20 minutes ago (Timeout is 15m)
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        this.aggregate.applyState("TELLER-01", true, past, TellerSessionAggregate.ScreenState.MAIN_MENU);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.sessionId = "SESSION-LOCKED";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set state to LOCKED to prevent navigation
        this.aggregate.applyState("TELLER-01", true, Instant.now(), TellerSessionAggregate.ScreenState.LOCKED);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // Verify it's a domain logic error (IllegalStateException fits best for invariant violations)
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException, got " + thrownException.getClass().getSimpleName());
        assertFalse(thrownException instanceof UnknownCommandException, "Should not be UnknownCommandException");
    }
}
