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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid for happy path
        aggregate.setLastActivityAt(Instant.now()); // Ensure active
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate setup, but we ensure command matches
        if (command == null) {
            command = new NavigateMenuCmd("session-123", "MAIN_MENU", "OPEN_ACCOUNT");
        }
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command setup
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command setup
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            if (command == null) {
                // Default setup for negative tests if command wasn't prepped
                command = new NavigateMenuCmd("session-123", "MAIN_MENU", "OPEN_ACCOUNT");
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("OPEN_ACCOUNT", event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // DO NOT call markAuthenticated(). Default is false.
        command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "OPEN_ACCOUNT");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        // You could also assert the message matches specific invariant text
        assertFalse(thrownException instanceof UnknownCommandException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        
        // Set last activity to 20 minutes ago (timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        
        command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "OPEN_ACCOUNT");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // This scenario implies business logic validation. 
        // We simulate this by passing an invalid command (e.g., blank action)
        // to the aggregate, or relying on the aggregate's internal logic to reject it.
        // However, the Aggregate logic for blank action is generic validation.
        // To specifically hit a "Context" error, we might rely on specific validation logic.
        // Based on our implementation, we pass invalid data in the command.
        
        String sessionId = "session-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        
        // Invalid context (blank menuId/action)
        command = new NavigateMenuCmd(sessionId, "", "OPEN_ACCOUNT"); 
    }
}