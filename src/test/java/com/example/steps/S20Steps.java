package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Scenario: Successfully execute EndSessionCmd ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.setCurrentNavigationState("IDLE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The aggregate is already initialized with a valid ID in the @Given step
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            var cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", resultEvents.get(0).type());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    // --- Scenario: EndSessionCmd rejected — A teller must be authenticated ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Not calling markAuthenticated(), so authenticated = false
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Should be IllegalStateException");
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // --- Scenario: EndSessionCmd rejected — Sessions must timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(); // Authenticated
        aggregate.markInactive(); // But timed out
    }

    // --- Scenario: EndSessionCmd rejected — Navigation state ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        // Set state that conflicts with ending (assumption: TXN_IN_PROGRESS blocks ending)
        aggregate.setCurrentNavigationState("TXN_IN_PROGRESS");
    }

}
