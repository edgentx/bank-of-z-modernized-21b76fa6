package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String testTellerId;
    private String testTerminalId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate successful authentication context for the positive case
        aggregate.setAuthenticated(true);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly set authenticated to false to violate the rule
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago (Violation: > 15 mins)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String sessionId = "session-nav-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        // Simulate a state where the navigation context is invalid or locked
        // In this specific aggregate impl, we verify the Command inputs are valid context.
        // However, if the aggregate itself holds a state that prevents context update:
        aggregate.setOperationalContext("INVALID_LOCKED_STATE");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.testTellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.testTerminalId = "TERM-01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), testTellerId, testTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(testTellerId, event.tellerId());
        assertEquals(testTerminalId, event.terminalId());
        
        // Verify aggregate state mutation
        assertTrue(aggregate.isSessionActive());
        assertEquals(testTerminalId, aggregate.getTerminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect a RuntimeException (usually IllegalStateException) 
        // containing the specific violation message defined in the aggregate.
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
