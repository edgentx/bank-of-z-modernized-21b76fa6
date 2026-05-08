package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String sessionId = "session-123";
    private String menuId = "MAIN_MENU";
    private String action = "ENTER";
    private String authContext = "teller-01";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Pre-authenticate to satisfy 'valid' state for success scenario
        aggregate.markAuthenticated(authContext);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is initialized in the field
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // menuId is initialized in the field
        assertNotNull(menuId);
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // action is initialized in the field
        assertNotNull(action);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action, authContext);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.sessionId());
        assertEquals(menuId, event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly do not authenticate
        authContext = null;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(authContext);
        // Helper to set last activity to the past
        aggregate.markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(authContext);
        // Set current menu to match the requested menu to trigger the context error
        aggregate.setCurrentMenuId("MAIN_MENU");
        this.menuId = "MAIN_MENU"; // Requesting same menu
        this.action = "ENTER";     // With standard action
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Depending on specific exception types, we check for IllegalStateException or IllegalArgumentException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
