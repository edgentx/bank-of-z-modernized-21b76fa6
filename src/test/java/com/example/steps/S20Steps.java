package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-20: EndSessionCmd.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "SESSION-123";
        this.tellerId = "TELLER-Alice";
        
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate to a valid, active state
        aggregate.markAuthenticated(tellerId);
        
        // Ensure clean state for other invariants
        aggregate.markInTransaction(false);
        aggregate.markScreenLocked(false);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // ID already set in previous step
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            var cmd = new EndSessionCmd(sessionId, tellerId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        assertNotNull(resultEvents, "Expected events list");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Expected SessionEndedEvent");
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals(sessionId, endedEvent.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "SESSION-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly mark as unauthenticated
        aggregate.markUnauthenticated();
        this.tellerId = null; // No valid teller
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "SESSION-TIMEDOUT";
        this.tellerId = "TELLER-Bob";
        this.aggregate = new TellerSessionAggregate(sessionId);
        
        // Mark authenticated but expired
        aggregate.markAuthenticated(tellerId);
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "SESSION-BADSTATE";
        this.tellerId = "TELLER-Charlie";
        this.aggregate = new TellerSessionAggregate(sessionId);
        
        aggregate.markAuthenticated(tellerId);
        // Simulate a state where a transaction is open (Invariant 3 violation)
        aggregate.markInTransaction(true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, 
                   "Expected IllegalStateException or IllegalArgumentException, but got: " + thrownException.getClass().getSimpleName());
        
        // Verify no events were committed
        assertNull(resultEvents, "No events should be emitted on failure");
    }
}
