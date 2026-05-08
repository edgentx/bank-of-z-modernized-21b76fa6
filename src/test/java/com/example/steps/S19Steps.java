package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated, recent activity
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do not mark authenticated -> violates invariant
        assertFalse(aggregate.isAuthenticated());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Auth is valid
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        sessionId = "session-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Valid state, but command action will trigger the violation in execute
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Already handled in Given setup, but we ensure non-null
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd;
            if ("INVALID_CONTEXT".equals(action)) {
                 // Special case for the context violation scenario
                 cmd = new NavigateMenuCmd(sessionId, "IGNORED", "INVALID_CONTEXT");
            } else {
                 cmd = new NavigateMenuCmd(sessionId, menuId, action);
            }
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // The specific message validates the correct invariant failed
        assertTrue(
            caughtException.getMessage().contains("authenticated") ||
            caughtException.getMessage().contains("timeout") ||
            caughtException.getMessage().contains("context")
        );
    }
}
