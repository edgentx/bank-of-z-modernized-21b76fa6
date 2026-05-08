package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario: Successfully execute NavigateMenuCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated("teller-001");
        this.aggregate.setCurrentMenu("HOME");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate construction above
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context setup handled in aggregate state
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context setup handled in aggregate state
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "ACCOUNTS", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNTS", event.menuId());
        assertNull(thrownException);
    }

    // Scenario: NavigateMenuCmd rejected — Authentication
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally NOT calling markAuthenticated
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("authenticated"));
    }

    // Scenario: NavigateMenuCmd rejected — Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.markAuthenticated("teller-002");
        // Force timeout
        this.aggregate.expireSession(); 
    }

    // Scenario: NavigateMenuCmd rejected — Context validity
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.aggregate = new TellerSessionAggregate("session-bad-ctx");
        this.aggregate.markAuthenticated("teller-003");
        // Set up a state that makes the transition invalid based on aggregate logic
        this.aggregate.setCurrentMenu("PUBLIC"); 
        // Note: The execute step below needs to use the specific invalid combo "ADMIN"
    }

    // Reusing the When clause from above
    
    // We need a specific When or override for the context violation to pass specific params
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        try {
            // Attempt to jump to ADMIN from PUBLIC (defined as invalid in Aggregate)
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-bad-ctx", "ADMIN", "GOTO");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}
