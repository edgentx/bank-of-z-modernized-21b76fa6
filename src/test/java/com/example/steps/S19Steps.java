package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.InitiateTellerSessionCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initiate it so it is valid and authenticated
        aggregate.execute(new InitiateTellerSessionCmd(sessionId, "teller-1", "terminal-1"));
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set in previous step or we default it
        if (sessionId == null) sessionId = "session-123";
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    // --- Scenarios for Invariant Violations ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "session-unauth";
        // Create aggregate but DO NOT initiate it, leaving it unauthenticated
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.execute(new InitiateTellerSessionCmd(sessionId, "teller-1", "terminal-1"));
        // Use helper to artificially expire the session
        aggregate.markAsExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        sessionId = "session-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.execute(new InitiateTellerSessionCmd(sessionId, "teller-1", "terminal-1"));
        // Setup inputs that will trigger the context violation check in the aggregate
        menuId = "ADMIN";
        action = "VIEW"; // Action does not match SUPERVISOR requirement
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We check for IllegalStateException which maps to Domain Error/Invariant violation in this pattern
        assertTrue(capturedException instanceof IllegalStateException);
    }
}