package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Initialize with a valid start event simulation or direct state setting for testing
        // For the aggregate to be valid, it must be authenticated, active, and not timed out.
        // We assume a mechanism to hydrate the aggregate exists, but for unit tests we can simulate a state.
        // However, the aggregate starts empty. We need a StartSessionCmd normally, but here we focus on End.
        // We will use a test helper or reflection to set internal state for the "valid" scenario, 
        // or we rely on the aggregate allowing the transition from Initialized -> Ended if valid.
        // Given the invariants: Authenticated, Not Timed out, Nav State OK.
        
        // To make the aggregate valid, we assume a session has started. 
        // Since we don't have StartSessionCmd in this story, we simulate a hydrated aggregate.
        aggregate.simulateHydration("teller-1", Instant.now(), "Idle");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Covered by the aggregate initialization
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-404");
        // Simulate a session that is NOT authenticated
        aggregate.simulateHydration(null, Instant.now(), "Idle");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a session that timed out (last activity 30 mins ago)
        aggregate.simulateHydration("teller-1", Instant.now().minus(Duration.ofMinutes(31)), "Idle");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Simulate a session with inconsistent navigation state
        aggregate.simulateHydration("teller-1", Instant.now(), "INVALID_STATE");
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull("Expected events to be emitted", resultEvents);
        assertFalse("Expected events list not to be empty", resultEvents.isEmpty());
        assertTrue("Expected SessionEndedEvent", resultEvents.get(0) instanceof SessionEndedEvent);
        assertNull("Expected no exception", capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull("Expected an exception to be thrown", capturedException);
        // Check if it's the specific domain error (IllegalStateException or IllegalArgumentException)
        assertTrue("Expected IllegalStateException or IllegalArgumentException",
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
