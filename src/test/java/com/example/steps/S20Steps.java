package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state
        aggregate.markActive(true);
        aggregate.markAuthenticated(true);
        aggregate.setCurrentScreen("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate construction in the previous step
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        executeCommand();
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Expected SessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    // --- Scenarios for Rejection ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markActive(true); 
        aggregate.markAuthenticated(false); // Violation: Not authenticated
        aggregate.setCurrentScreen("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markActive(true);
        aggregate.markAuthenticated(true);
        aggregate.setCurrentScreen("IDLE");
        // Violation: Last activity was 31 minutes ago (Timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "SESSION-BUSY";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markActive(true);
        aggregate.markAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        // Violation: Screen is in a state that doesn't allow termination
        aggregate.setCurrentScreen("TRANSACTION_IN_PROGRESS");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // Check it's a specific domain rule violation (IllegalStateException is used in the aggregate)
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify NO events were emitted due to failure
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "Expected no events on failure");
    }

    // Helper
    private void executeCommand() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id(), Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
