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
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure it is in a valid state by default
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate construction
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be used in command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be used in command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // Default is !authenticated, so no action needed
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.markNavigationInvalid();
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            command = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("MAIN_MENU", event.menuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
