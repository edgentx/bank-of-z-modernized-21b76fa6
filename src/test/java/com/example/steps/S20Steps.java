package com.example.steps;

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

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private static final String SESSION_ID = "SESSION-123";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated("TELLER-42");
        aggregate.setNavigationContext("MAIN_MENU");
        aggregate.setLastActivityAt(Instant.now());
        caughtException = null;
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The session ID is handled by the aggregate constructor in this test setup
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(SESSION_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals(SESSION_ID, endedEvent.aggregateId());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Intentionally not calling markAuthenticated() so isActive = false / tellerId = null
        caughtException = null;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Should be IllegalStateException");
        
        // Ensure no events were committed if error occurred
        assertNull(resultEvents); 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated("TELLER-42");
        aggregate.setNavigationContext("MAIN_MENU");
        // Set last activity to 20 minutes ago (timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        caughtException = null;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated("TELLER-42");
        // Intentionally not setting navigation context, or setting it to null
        aggregate.setNavigationContext(null);
        caughtException = null;
    }

}
