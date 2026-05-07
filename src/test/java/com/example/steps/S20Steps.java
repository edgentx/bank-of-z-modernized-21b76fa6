package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup: Authenticate and set valid state to meet invariants for success path
        aggregate.markAuthenticated("teller-007");
        aggregate.setNavigationState("MENU_IDLE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set in previous step
        Assertions.assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
        Assertions.assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "session-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated(). isAuthenticated defaults to false.
        // Must still be active to test this specific invariant
        aggregate.setNavigationState("MENU_IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-007");
        aggregate.setNavigationState("MENU_IDLE");
        // Simulate time passing beyond threshold
        aggregate.simulateInactivity();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "session-nav-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-007");
        // Set a state that prevents closing (e.g., mid-transaction)
        aggregate.setNavigationState("TXN_DEPOSIT_IN_PROGRESS");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertTrue(resultEvents.isEmpty(), "No events should be produced on failure");
        // Verify aggregate state did not change (isActive)
        Assertions.assertTrue(aggregate.isActive(), "Aggregate should remain active on rejection");
    }
}