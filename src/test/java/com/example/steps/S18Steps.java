package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private String validTellerId = "TELLER-001";
    private String validTerminalId = "TERM-42";

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // Defaults: authenticated, valid nav state, active=false
        aggregate.markAuthenticated(true);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Using default constant validTellerId
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Using default constant validTerminalId
    }

    // --- Violation Givens ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "SESSION-AUTH-FAIL";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(false); // Violation: not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        // Simulate a session that was active long ago
        // Assuming SESSION_TIMEOUT is 30 mins in aggregate
        Instant past = Instant.now().minus(Duration.ofHours(1));
        aggregate.markLastActivity(past);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "SESSION-NAV-FAIL";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.markNavigationInvalid(true); // Violation
    }

    // --- Whens ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertFalse(resultEvents.isEmpty(), "Expected list of events to be non-empty");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session.started", started.type());
        assertEquals(aggregate.id(), started.aggregateId());
        assertEquals(validTellerId, started.tellerId());
        assertEquals(validTerminalId, started.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown, but none was");
        // Checking for IllegalStateException or general RuntimeException depending on implementation preference.
        // The prompt examples use IllegalStateException for domain errors.
        assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException, but got " + caughtException.getClass().getSimpleName());
        
        // Ensure no events were emitted on failure
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on command rejection");
    }
}