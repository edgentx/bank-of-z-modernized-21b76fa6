package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // 1. Valid Session
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state: Authenticated and Not Timed Out
        aggregate.markAuthenticated(); 
        // Context is null or idle
        aggregate.setCurrentContext("IDLE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(sessionId);
        assertEquals(aggregate.id(), sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);
        assertEquals("session.ended", resultEvents.get(0).type());
    }

    // 2. Rejected - Not Authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: isAuthenticated is false by default
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected exception");
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("not authenticated"));
    }

    // 3. Rejected - Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Auth is valid
        // Violation: Set last activity to 20 minutes ago
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    // Then step is shared above

    // 4. Rejected - Navigation State Context
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "SESSION-BUSY";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Violation: Context indicates active operation (preventing termination)
        aggregate.setCurrentContext("CASH_WITHDRAWAL");
    }
    // Then step is shared above
}
