package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.model.TellerSessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSession session;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        session = new TellerSession("session-123");
        // Assume the aggregate was previously initiated and authenticated successfully
        session.markAuthenticated();
        assertNotNull(session);
        assertTrue(session.isAuthenticated());
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the constructor in the previous step
        // We assume the command will be created with the session's ID
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        session = new TellerSession("session-unauth-456");
        // Do NOT call markAuthenticated(). authenticated remains false.
        assertFalse(session.isAuthenticated());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        session = new TellerSession("session-timeout-789");
        session.markAuthenticated(); // Valid initially
        session.simulateInactivity(); // Helper to make lastActivityAt old
        // Check internal state (testing infrastructure)
        // Since hasTimedOut is private, we rely on the execute() step to throw the exception
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        session = new TellerSession("session-nav-error-101");
        session.markAuthenticated();
        session.markNavigationStateStale(); // Simulate the violation
        assertTrue(session.isNavigationStateStale());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(session.id());
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent, "Expected TellerSessionEndedEvent");
        
        // Verify the aggregate state changed
        assertFalse(session.isActive(), "Session should be inactive");
        assertFalse(session.isAuthenticated(), "Session should be cleared (unauthenticated)");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but none was");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
