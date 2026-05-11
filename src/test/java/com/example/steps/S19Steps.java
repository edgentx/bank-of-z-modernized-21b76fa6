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
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {
    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private static final String VALID_SESSION_ID = "session-123";

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Default to a valid state: authenticated and active
        aggregate.authenticate("teller-001");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Do not authenticate - stays in invalid state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.authenticate("teller-001");
        // Force expiration
        aggregate.expireSession();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.authenticate("teller-001");
        // Force a locked state (simulating context mismatch)
        aggregate.lockSession();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by aggregate construction
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // We will define this in the 'When' step construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // We will define this in the 'When' step construction
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            command = new NavigateMenuCmd(VALID_SESSION_ID, "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Expected events to be non-null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception (IllegalStateException or UnknownCommandException)");
        // Verify it's the right kind of error (IllegalStateException is used for invariants in our aggregate)
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}