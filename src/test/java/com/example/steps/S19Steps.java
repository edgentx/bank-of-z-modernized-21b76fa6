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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure pre-conditions for success are met
        aggregate.setOperationalContext("TRANSACTION");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the aggregate constructor above
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by command construction in When
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by command construction in When
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "TRANSACT");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
    }

    // Scenario 2: Auth Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Default state is unauthenticated, so we leave it as is.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Exception should be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException");
        assertTrue(thrownException.getMessage().contains("authenticated"), "Error message should mention authentication");
    }

    // Scenario 3: Timeout Failure
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markInactive(); // Force the last activity time to be old
    }

    // Scenario 4: Context Failure
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        aggregate.markAuthenticated();
        aggregate.setOperationalContext("INQUIRY"); // Set context that doesn't match "TRANSACT" action in When step
        // Note: The When step uses "TRANSACT" action which requires "TRANSACTION" context
    }

}
