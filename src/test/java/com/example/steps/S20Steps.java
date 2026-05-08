package com.example.steps;

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
    private Exception capturedException;

    // Helper to create a valid aggregate
    private TellerSessionAggregate createValidAggregate() {
        String sessionId = "session-123";
        TellerSessionAggregate agg = new TellerSessionAggregate(sessionId);
        // Force aggregate into valid authenticated state for positive test
        agg.activate("teller-001");
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by 'aValidTellerSessionAggregate' above, no extra step needed.
        // But we could explicitly set the ID here if the aggregate wasn't already built.
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        executeCommand();
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertFalse(aggregate.isActive(), "Aggregate should be inactive after event");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.setAuthenticated(false); // Explicitly unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true); // It is authenticated
        // Set last activity to 20 minutes ago (assuming timeout is 15m)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.setAuthenticated(true);
        aggregate.setNavigationState("PENDING_TRANSACTION"); // Simulate invalid state for logout
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // --- Private Helper ---

    private void executeCommand() {
        String tellerId = (aggregate.getTellerId() != null) ? aggregate.getTellerId() : "unknown-teller";
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id(), tellerId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
