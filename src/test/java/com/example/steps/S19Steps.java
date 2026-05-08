package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {
    private TellerSessionAggregate aggregate;
    private String providedSessionId;
    private String providedMenuId;
    private String providedAction;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        providedSessionId = "sess-123";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.clearEvents();
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        providedSessionId = "sess-123";
        // Re-assert aggregate just in case step order is shuffled, though normally sequential
        if (aggregate == null) {
            aggregate = new TellerSessionAggregate(providedSessionId);
            aggregate.markAuthenticated();
        }
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        providedMenuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        providedAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(providedSessionId, providedMenuId, providedAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(providedSessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Domain errors are modeled as RuntimeExceptions (IllegalStateException/IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Scenario 2 Violations
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("sess-violate-auth");
        // Auth defaults to false
    }

    // Scenario 3 Violations
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("sess-violate-timeout");
        aggregate.markAuthenticated(); // Must be valid otherwise
        aggregate.markExpired();
    }

    // Scenario 4 Violations
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("sess-violate-state");
        aggregate.markAuthenticated();
        aggregate.markLocked(); // Simulating inconsistent/locked context
    }
}
