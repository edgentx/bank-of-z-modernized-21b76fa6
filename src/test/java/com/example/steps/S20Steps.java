package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();       // Ensure auth invariant passes
        aggregate.validateNavigationState(); // Ensure nav invariant passes
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // ID is already set in the constructor via the Given step
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    // ------------------------------
    // Negative Scenarios
    // ------------------------------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated()
        aggregate.validateNavigationState(); // Ensure other invariants pass so we isolate this one
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.validateNavigationState();
        aggregate.expireSession(); // Force timeout state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String sessionId = "session-nav-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.invalidateNavigationState(); // Intentionally NOT validating state
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Checking that it is an IllegalStateException (which we use for domain rule violations)
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
