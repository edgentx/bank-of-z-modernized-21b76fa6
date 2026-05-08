package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123", "teller-01");
        aggregate.markAuthenticated(); // Set valid state
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate construction in previous step
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be used in the command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be used in the command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-999", "teller-02");
        // NOT calling markAuthenticated() leaves it unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout", "teller-03");
        aggregate.markAuthenticated();
        // Simulate timeout
        aggregate.simulateInactivity(Duration.ofMinutes(31)); // Default timeout is 30m
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-error", "teller-04");
        aggregate.markAuthenticated();
        aggregate.forceInvalidNavigationContext(); // Force state corruption
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Check for typical domain exception types or message content
        Assertions.assertTrue(thrownException instanceof IllegalStateException || 
                              thrownException instanceof IllegalArgumentException);
    }

}
