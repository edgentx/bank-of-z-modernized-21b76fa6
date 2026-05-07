package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
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

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // "Valid" means authenticated, active, and in a valid context.
        aggregate = new TellerSessionAggregate("SESSION-1");
        aggregate.initialize("TELLER-101", Instant.now().minusSeconds(60)); // Active 60s ago
        aggregate.authenticate(); // Set authenticated = true
        aggregate.setCurrentMenu("MAIN_MENU"); // Set valid context
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by creating the aggregate in the Given step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Used in the When step construction of the command
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Used in the When step construction of the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("SESSION-1", "ACCOUNTS_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(MenuNavigatedEvent.TYPE, resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        aggregate.initialize("TELLER-102", Instant.now());
        // DO NOT call authenticate(). It remains false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        // Initialize last active time 31 minutes ago (Timeout is 30 mins)
        aggregate.initialize("TELLER-103", Instant.now().minus(Duration.ofMinutes(31)));
        aggregate.authenticate();
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-STATE");
        aggregate.initialize("TELLER-104", Instant.now());
        aggregate.authenticate();
        // DO NOT set a current menu (simulate corrupted state)
        aggregate.invalidateNavigationState();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // In this domain model, violations are enforced by throwing IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}