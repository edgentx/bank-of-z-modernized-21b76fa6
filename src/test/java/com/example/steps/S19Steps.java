package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermenu.model.NavigateMenuCmd;
import com.example.domain.tellermenu.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-123";
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
        
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate a valid session setup for a happy path
        this.aggregate.markAuthenticated();
        this.aggregate.updateLastActivity(Instant.now());
        this.aggregate.setCurrentContext("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        assertNotNull(this.menuId);
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        assertNotNull(this.action);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull("Expected no exception, but got: " + caughtException, caughtException);
        assertNotNull("Expected events to be emitted", resultEvents);
        assertFalse("Expected at least one event", resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "sess-unauth";
        this.menuId = "LOGIN_SCREEN";
        this.action = "SUBMIT";
        
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Do NOT mark authenticated. The internal check should fail.
        this.aggregate.updateLastActivity(Instant.now());
        this.aggregate.setCurrentContext("LOGIN_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";

        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated();
        // Set activity to past beyond timeout window
        this.aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(2)));
        this.aggregate.setCurrentContext("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.sessionId = "sess-bad-ctx";
        this.menuId = "SCREEN_X";
        this.action = "ENTER";

        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.updateLastActivity(Instant.now());
        // Simulate the aggregate being in a different context than allowed for the command
        // or strictly enforcing a transition logic. Here we set context to 'LOCKED'
        this.aggregate.setCurrentContext("LOCKED_SCREEN");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull("Expected a domain error exception", caughtException);
        assertTrue("Expected IllegalStateException or IllegalArgumentException", 
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        assertNull("Expected no events due to rejection", resultEvents);
    }
}
