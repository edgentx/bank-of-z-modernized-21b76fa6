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
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Set up valid state (authenticated, active)
        aggregate.markAuthenticated("teller-001");
        aggregate.setLastActivity(Instant.now()); // Ensure not timed out
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled by the aggregate ID in this design, 
        // but we ensure it matches or is valid as per the command setup later.
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "session-456";
        aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly mark as unauthenticated to violate the invariant
        aggregate.markUnauthenticated(); 
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-789";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        // Set activity to a time well past the threshold
        aggregate.setLastActivity(Instant.now().minus(Duration.ofHours(1)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        String sessionId = "session-101";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        aggregate.setLastActivity(Instant.now());
        // Invalidate the navigation state
        aggregate.invalidateContext();
    }
}
