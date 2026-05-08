package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermessaging.model.NavigateMenuCmd;
import com.example.domain.tellermessaging.model.MenuNavigatedEvent;
import com.example.domain.tellermessaging.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String sessionId;
    private String menuId;
    private String action;

    // --- Happy Path Setup ---
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a previous Login to ensure Authenticated state
        aggregate.handlePastLogin("teller123", Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set in aggregate setup
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "SELECT_ACCOUNT";
    }

    // --- Unauthenticated Setup ---
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        // Create aggregate but DO NOT call handlePastLogin. isAuthenticated will be false.
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    // --- Timeout Setup ---
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated, but last interaction was long ago
        aggregate.handlePastLogin("teller123", Instant.now().minus(Duration.ofHours(2)));
    }

    // --- Context Violation Setup ---
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.handlePastLogin("teller123", Instant.now());
        // Forcing a context mismatch, e.g., trying to perform a withdrawal action
        // while in a read-only menu context (simulated by specific state setup in aggregate)
        aggregate.forceContextViolation();
    }

    // --- Execution ---
    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action, Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---
    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.targetMenuId());
        assertEquals(action, event.actionTaken());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We check for specific exceptions or general Illegal State/Argument exceptions as domain errors
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Suite Runner
    @org.junit.platform.suite.api.Suite
    @org.junit.platform.suite.api.SelectClasspathResource("features/S-19.feature")
    @org.junit.platform.suite.api.IncludeEngines("cucumber")
    public static class S19TestSuite {}

}
