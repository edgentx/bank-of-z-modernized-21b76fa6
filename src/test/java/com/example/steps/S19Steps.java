package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to reset state before scenarios if necessary, though Cucumber usually instantiates fresh.

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Default to authenticated for success case
        this.aggregate.markAuthenticated();
        this.aggregate.setCurrentContext("ROOT");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-unauth");
        // Ensure authenticated is false (default)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.markAuthenticated();
        this.aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-bad-context");
        this.aggregate.markAuthenticated();
        // Set a context that might reject a specific action in the domain logic
        this.aggregate.setCurrentContext("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate construction in Given steps
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in When step construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in When step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // We construct the command. In the 'valid' context, we pick standard values.
            // In the 'violates navigation state' context, the logic inside the aggregate
            // checks the current context against the action.
            
            // For the specific violation test, we use an action that conflicts with MAIN_MENU
            // as defined in the aggregate logic (F3 on MAIN_MENU). 
            String action = "ENTER";
            if ("MAIN_MENU".equals(aggregate.getCurrentContext())) {
               // Triggering the invalid state logic defined in the stub aggregate
               action = "F3"; 
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "NEXT_SCREEN", action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Domain errors typically manifest as IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
