package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it is authenticated and active for the success case
        aggregate.markAuthenticated("teller-456");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "session-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly ensure not authenticated
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Must be authenticated but expired
        aggregate.markAuthenticated("teller-456");
        aggregate.markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "session-nav-ctx";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-456");
        // Set current menu to the one we will try to navigate to, causing a violation
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.sessionId = "session-123";
        // ID is already set in aggregate construction, this confirms we have it
        assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "DEPOSIT_SCREEN";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.caughtException = e;
        } catch (UnknownCommandException e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertEquals("DEPOSIT_SCREEN", ((MenuNavigatedEvent) resultEvents.get(0)).menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // In this domain, we use RuntimeExceptions (IllegalStateException/IllegalArgumentException) to model domain errors/rejections
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        System.out.println("Caught expected domain error: " + caughtException.getMessage());
    }
}