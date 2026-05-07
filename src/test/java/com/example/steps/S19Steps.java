package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup valid base state
        aggregate.setAuthenticated(true);
        aggregate.setCurrentMenuId("MAIN_MENU");
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setLocked(false);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled by the aggregate construction in the previous step
        // But we could assert the aggregate ID here if we were extracting it to a context variable
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Valid menuId is part of the Command construction in 'When'
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Valid action is part of the Command construction in 'When'
    }

    // --- Violations (Negative Givens) ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setCurrentMenuId("MAIN_MENU");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.setAuthenticated(true);
        aggregate.setCurrentMenuId("MAIN_MENU");
        // Set last activity to 20 minutes ago (assuming default timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        this.aggregate = new TellerSessionAggregate("session-context-fail");
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentMenuId("LOCKED_SCREEN"); // Context is Locked
        aggregate.setLocked(true);
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Construct the command with valid defaults unless the specific scenario implies otherwise
            // Note: Cucumber runs the 'Given violation' first, so the aggregate state is already set up for failure.
            NavigateMenuCmd cmd = new NavigateMenuCmd(
                aggregate.id(), 
                "ACCOUNT_DETAIL", 
                "VIEW"
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // The spec says 'domain error'. In Java DDD, this is usually an IllegalStateException or IllegalArgumentException.
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
