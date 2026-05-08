package com.example.steps;

import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "SESSION-123";
        // Create a valid, authenticated, active session
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate the aggregate to a valid state as if created via StartSessionCmd
        // In a real repo scenario, this would be loaded from events.
        aggregate.hydrateForTest("TELLER-101", Instant.now().minusSeconds(60), true, "/dashboard");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is already set in the aggregate via the constructor in previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "SESSION-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Force state to unauthenticated/null teller
        aggregate.hydrateForTest(null, Instant.now(), true, "/login");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "SESSION-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Last activity was 20 minutes ago (assuming timeout is 15)
        aggregate.hydrateForTest("TELLER-102", Instant.now().minusSeconds(1200), true, "/idle");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "SESSION-BADNAV";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Valid state, but invalid navigation context for ending (e.g. stuck in a transaction)
        aggregate.hydrateForTest("TELLER-103", Instant.now(), true, "/transaction/processing");
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals("teller.session.ended", event.type());
        
        // Verify aggregate state mutation
        assertFalse(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Depending on invariant, it might be IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
