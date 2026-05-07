package com.example.steps;

import com.example.domain.tellermenu.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Simulate a fully initialized, authenticated state by applying events directly or setting state
        aggregate.rehydrate(
            true, 
            Instant.now().plusSeconds(300), // 5 mins from now
            "MAIN_MENU"
        );
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the aggregate initialization in the previous step
        // The command will be constructed with this ID in the When step
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context for the command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context for the command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("SESSION-1", "ACCOUNT_SUMMARY", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        aggregate.rehydrate(false, Instant.now().plusSeconds(300), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        aggregate.rehydrate(true, Instant.now().minusSeconds(10), "MAIN_MENU"); // Expired
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("SESSION-BADNAV");
        aggregate.rehydrate(true, Instant.now().plusSeconds(300), "UNKNOWN_STATE");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for specific exception types if necessary (IllegalStateException, IllegalArgumentException)
    }
}
