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
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        
        // Setup valid state: authenticated, active, recent activity, valid context
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setOperationalContext("MAIN_MENU");
        
        this.thrownException = null;
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Already handled in the setup above, but we ensure ID matches.
        assertNotNull(sessionId);
        assertEquals(sessionId, aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "session-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Not authenticated
        aggregate.setAuthenticated(false);
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setOperationalContext("MAIN_MENU");
        
        this.thrownException = null;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Valid state otherwise
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setOperationalContext("MAIN_MENU");
        
        // Violation: Expired timestamp (20 minutes ago)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        
        this.thrownException = null;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "session-nav-bad";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Valid state otherwise
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
        
        // Violation: Null or blank navigation context
        aggregate.setOperationalContext(null);
        
        this.thrownException = null;
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent);
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals(sessionId, endedEvent.aggregateId());
        assertNotNull(endedEvent.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // In this domain logic, we throw IllegalStateException for invariant violations
        assertTrue(thrownException instanceof IllegalStateException);
        
        // Ensure no events were emitted
        assertNull(resultEvents);
    }
}
