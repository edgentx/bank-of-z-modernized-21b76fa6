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

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private NavigateMenuCmd cmd;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-1");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by constructor in aggregate setup
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Defined in the command setup
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Defined in the command setup
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        cmd = new NavigateMenuCmd("session-123", "DEPOSIT", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals(MenuNavigatedEvent.class, resultEvents.get(0).getClass());
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do not authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-1");
        aggregate.expireSession();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.markAuthenticated("teller-1");
        // Will target invalid menu ID in the When step for this scenario
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        
        // Check specific error messages based on setup
        String msg = caughtException.getMessage();
        assertTrue(msg.contains("A teller must be authenticated") || 
                   msg.contains("Sessions must timeout") || 
                   msg.contains("Navigation state must accurately reflect"));
    }

    // Additional step for invalid context scenario
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        cmd = new NavigateMenuCmd("session-bad-nav", "INVALID_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
