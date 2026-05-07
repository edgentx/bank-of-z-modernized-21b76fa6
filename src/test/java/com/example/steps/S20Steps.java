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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Simulating a healthy, active session
        String sessionId = "ts-123";
        String tellerId = "teller-01";
        Instant lastActivity = Instant.now();
        String navState = "DASHBOARD";
        
        aggregate = new TellerSessionAggregate(sessionId);
        // Using reflection or a test-specific setup method to hydrate state without invoking Command logic
        // Ideally, we'd load from history, but for unit steps we hydrate directly.
        aggregate.__internalHydrate(sessionId, tellerId, true, lastActivity, navState);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "ts-invalid-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.__internalHydrate(sessionId, "teller-01", false, Instant.now(), "DASHBOARD");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "ts-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate last activity 30 minutes ago (configured timeout is usually 15)
        Instant oldActivity = Instant.now().minus(Duration.ofMinutes(30));
        aggregate.__internalHydrate(sessionId, "teller-01", true, oldActivity, "DASHBOARD");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String sessionId = "ts-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        // Current navigation state does not match expected context (e.g. stuck in a transaction screen)
        aggregate.__internalHydrate(sessionId, "teller-01", true, Instant.now(), "BAD_STATE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by aggregate construction in Given steps
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull("Should not throw exception", capturedException);
        assertNotNull("Events should not be null", resultEvents);
        assertEquals("Should emit one event", 1, resultEvents.size());
        assertTrue("Should be SessionEndedEvent", resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull("Expected exception to be thrown", capturedException);
        assertTrue("Expected IllegalStateException", capturedException instanceof IllegalStateException);
    }
}
