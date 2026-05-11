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
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = TellerSessionAggregate.createAuthenticated("session-123", "teller-01");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The aggregate is initialized with a valid ID in the previous step
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = TellerSessionAggregate.createAuthenticated("session-123", "teller-01");
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = TellerSessionAggregate.createAuthenticated("session-123", "teller-01");
        aggregate.simulateTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = TellerSessionAggregate.createAuthenticated("session-123", "teller-01");
        aggregate.forceNavigationState("TRANSACTION_PENDING");
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd("session-123");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected exception was not thrown");
        // Verify it's the specific invariant violation
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
