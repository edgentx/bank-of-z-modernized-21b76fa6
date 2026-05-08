package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private Exception thrownException;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Valid session: authenticated, active, valid context
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate an authenticated state (in a real app, this would be via event sourcing hydration)
        aggregate.markAuthenticated();
        aggregate.updateLastActivity(); 
        aggregate.markContextValid();
        this.sessionId = "session-123";
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID implicitly provided via the aggregate ID
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the When step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Create command with valid data
            command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
            aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        var events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(events.getLast() instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        var event = (MenuNavigatedEvent) events.getLast();
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("MAIN_MENU", event.menuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Do NOT mark authenticated
        this.sessionId = "session-401";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated(); 
        aggregate.markTimedOut(); // Simulate timeout
        this.sessionId = "session-408";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-409");
        aggregate.markAuthenticated();
        aggregate.markContextInvalid(); // Simulate invalid context
        this.sessionId = "session-409";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Check if it's the specific type or a general Illegal State/Argument exception
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || 
            thrownException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException), but got: " + thrownException.getClass().getName()
        );
    }
}