package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd.NavigateMenuCmdBuilder cmdBuilder;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    public S19Steps() {
        // Initialize default valid command builder
        this.cmdBuilder = NavigateMenuCmd.builder()
                .sessionId("session-123")
                .menuId("MAIN_MENU")
                .action("ENTER")
                .tellerId("teller-01")
                .isAuthenticated(true)
                .lastActivityTimestampMillis(System.currentTimeMillis())
                .configuredTimeoutMillis(300000) // 5 mins
                .isNavigationContextValid(true);
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        cmdBuilder.sessionId("session-123");
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        cmdBuilder.menuId("TX_MENU");
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        cmdBuilder.action("SELECT");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        cmdBuilder.sessionId("session-auth-fail").isAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Set last activity to 1 hour ago
        cmdBuilder.sessionId("session-timeout-fail")
                .lastActivityTimestampMillis(System.currentTimeMillis() - 3600 * 1000)
                .configuredTimeoutMillis(300000);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        this.aggregate = new TellerSessionAggregate("session-context-fail");
        cmdBuilder.sessionId("session-context-fail").isNavigationContextValid(false);
    }

    // --- Execution ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = cmdBuilder.build();
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    // --- Assertions ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("TX_MENU", event.menuId());
        assertEquals("SELECT", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        
        String message = thrownException.getMessage();
        assertTrue(message.length() > 0, "Error message should not be empty");
        
        // Ensure no events were emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on failure");
    }
}
