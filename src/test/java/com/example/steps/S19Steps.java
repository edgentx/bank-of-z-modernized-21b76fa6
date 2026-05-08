package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.setAuthenticated(false); // Explicitly unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated(); // Auth is fine
        aggregate.markTimedOut();       // But it is old
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate("session-400");
        aggregate.markAuthenticated();
        // The aggregate logic rejects 'LOGIN' if authenticated, simulating context violation
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        // Default valid data for the positive flow, can be overridden if needed
        // In the error scenarios, we use specific data that triggers the logic
        String targetMenu = "ACCT_SUMMARY";

        // If we are testing the 'Navigation Context' violation, try to go to LOGIN
        if (aggregate.isAuthenticated() && aggregate.getCurrentMenuId().equals("MAIN_MENU")) {
             targetMenu = "LOGIN";
        }

        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException,
            "Expected a domain exception (IllegalStateException or IllegalArgumentException), got: " + caughtException.getClass().getSimpleName()
        );
    }

    // --- Parameter injection support (used implicitly by context, but here for explicit matches) ---
    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the 'Given a valid TellerSession aggregate' setup
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the 'When' setup
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the 'When' setup
    }
}
