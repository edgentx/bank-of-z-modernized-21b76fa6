package com.example.steps;

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

    private TellerSessionAggregate session;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        session = new TellerSessionAggregate("session-123");
        session.markAuthenticated("teller-01"); // Setup valid state
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate construction
        assertNotNull(session.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be provided in the command execution step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be provided in the command execution step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(session.id(), "MAIN_MENU", "ENTER");
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        session = new TellerSessionAggregate("session-unauth");
        // Intentionally NOT calling markAuthenticated
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Ideally check for specific domain exception type, here checking general behavior
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        session = new TellerSessionAggregate("session-timeout");
        session.markAuthenticated("teller-timeout");
        session.markExpired(); // Simulate time passing
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        session = new TellerSessionAggregate("session-bad-state");
        session.markAuthenticated("teller-bad-state");
        // We test the invariant by providing invalid commands in the test context
        // But strictly speaking, the aggregate state itself is valid, the request is what violates it.
        // However, adhering to the prompt's setup structure:
    }

    // Specialized when for the Navigation State violation (passing nulls)
    @When("the NavigateMenuCmd command is executed with invalid state")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidState() {
        try {
            // Passing null menuId violates the "Navigation state must accurately reflect..." invariant
            NavigateMenuCmd cmd = new NavigateMenuCmd(session.id(), null, "ENTER");
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}