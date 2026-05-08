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

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentSessionId;
    private String currentTellerId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        currentSessionId = "sess-123";
        currentTellerId = "teller-01";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Simulate a valid, active session state via reflection or a testing setup method if available.
        // Here we assume the aggregate starts in a state that allows initialization, but for the sake 
        // of the EndSessionCmd test, we need to ensure the session is started.
        // Since we don't have StartSessionCmd implemented, we simulate the state directly for this exercise.
        
        // In a real test, we would execute a StartSessionCmd.
        // For this stub, we'll assume the constructor or a test helper puts it in a valid state 
        // where isAuthenticated = true, isActive = true.
        aggregate.injectTestState(currentTellerId, true, true, Instant.now().minusSeconds(60), "MENU_MAIN");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(currentSessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(currentSessionId, currentTellerId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should produce exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(currentSessionId, event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        currentSessionId = "sess-unauth";
        currentTellerId = null; // Violation
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.injectTestState(null, false, true, Instant.now(), "MENU_MAIN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        currentSessionId = "sess-timeout";
        currentTellerId = "teller-01";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Last active time 2 hours ago (assuming timeout is < 2 hours)
        aggregate.injectTestState(currentTellerId, true, true, Instant.now().minus(Duration.ofHours(2)), "MENU_MAIN");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        currentSessionId = "sess-nav-bad";
        currentTellerId = "teller-01";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Violation: Active but in a 'TERMINATED' or invalid navigation state
        aggregate.injectTestState(currentTellerId, true, true, Instant.now(), "TERMINATED");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException, 
                   "Expected domain error (IllegalStateException or IllegalArgumentException)");
    }
}
