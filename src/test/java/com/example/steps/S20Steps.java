package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionAggregate.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate.SessionEndedEvent;
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
        aggregate = new TellerSessionAggregate("session-123");
        // Setup valid authenticated state
        aggregate.markAuthenticated("teller-001");
        // Setup valid navigation state
        aggregate.setCurrentScreen("HOME");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled by the aggregate instance in these steps
        // The command constructed in 'When' will use the aggregate's ID.
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", resultEvents.get(0).type());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.markUnauthenticated();
        // Simulate active but not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-001");
        // Set last activity to 20 minutes ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
        // Set threshold to 15 minutes
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated("teller-001");
        // Set a navigation state that prevents closure
        aggregate.setCurrentScreen("CRITICAL_TXN_PENDING");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
