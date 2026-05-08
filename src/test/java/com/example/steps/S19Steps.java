package com.example.steps;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId, SESSION_TIMEOUT);
        aggregate.markAuthenticated("TELLER-01"); // Ensure authenticated
        aggregate.clearEvents(); // Clean setup events
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate initialization, verify state
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command creation later
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command creation later
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Create command with defaults valid for a 'success' scenario if not set explicitly
            if (command == null) {
                command = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            }
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "SESSION-401";
        aggregate = new TellerSessionAggregate(sessionId, SESSION_TIMEOUT);
        // Intentionally do NOT call markAuthenticated
        command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "SESSION-408";
        aggregate = new TellerSessionAggregate(sessionId, SESSION_TIMEOUT);
        aggregate.markAuthenticated("TELLER-02");
        aggregate.expireSession(); // Simulate time passing
        command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        String sessionId = "SESSION-409";
        aggregate = new TellerSessionAggregate(sessionId, SESSION_TIMEOUT);
        aggregate.markAuthenticated("TELLER-03");
        aggregate.lockSystem(); // Simulate system lock
        command = new NavigateMenuCmd(sessionId, "TRANSACTION_ENTRY", "ENTER");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Should be an IllegalStateException (Domain Error)");
        assertTrue(caughtException.getMessage().contains("must") || caughtException.getMessage().contains("context"),
                "Exception message should indicate the violation: " + caughtException.getMessage());
    }
}
