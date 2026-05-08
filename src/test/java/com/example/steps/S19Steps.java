package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Duration configuration for timeout tests
    private static final Duration TIMEOUT = Duration.ofMinutes(15);

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123", TIMEOUT);
        aggregate.markAuthenticated(); // Ensure valid state for positive case
        aggregate.resetLastActivity();  // Ensure not expired
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in constructor of the aggregate or command creation
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command creation
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command creation
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth", TIMEOUT);
        // 'authenticated' is false by default
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout", TIMEOUT);
        aggregate.markAuthenticated(); // Ensure auth doesn't fail first
        aggregate.expireSession();     // Set last activity to past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContextState() {
        aggregate = new TellerSessionAggregate("session-bad-state", TIMEOUT);
        aggregate.markAuthenticated();
        aggregate.setInvalidContextState(); // Simulate invalid state
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DEPOSIT_SCREEN", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals("DEPOSIT_SCREEN", event.targetMenuId());
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // In this domain implementation, invariants are enforced by throwing IllegalStateException
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
