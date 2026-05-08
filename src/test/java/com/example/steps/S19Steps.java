package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute NavigateMenuCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by construction in previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be handled in the When step via command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be handled in the When step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
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
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    // Scenario: NavigateMenuCmd rejected — Authentication
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.markUnauthenticated();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }

    // Scenario: NavigateMenuCmd rejected — Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(); // Auth is valid
        aggregate.expireSession();      // But time has expired
    }

    // Scenario: NavigateMenuCmd rejected — Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-inactive");
        aggregate.markAuthenticated();
        aggregate.markSessionInactive(); // Context violation (e.g. already logged out elsewhere)
    }
}
