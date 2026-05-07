package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated("teller-001");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Do not authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated("teller-001");
        this.aggregate.markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated("teller-001");
        this.aggregate.invalidateNavigationContext();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in command construction
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            String menuId = "MAIN_MENU";
            String action = "ENTER";
            this.command = new NavigateMenuCmd(aggregate.id(), menuId, action);
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }
}