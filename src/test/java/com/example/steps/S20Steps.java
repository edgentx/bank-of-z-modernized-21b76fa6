package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.TellerSessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20: EndSessionCmd on TellerSession.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String providedSessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        providedSessionId = "session-123";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Pre-conditions for valid session: Authenticated and Active
        aggregate.markAuthenticated(); 
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The ID was set in the previous step. We ensure it matches.
        assertNotNull(providedSessionId);
        assertEquals(providedSessionId, aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        providedSessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Intentionally NOT calling markAuthenticated() to violate the invariant.
        // The aggregate defaults to isAuthenticated=false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        providedSessionId = "session-timeout-fail";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated(); // Ensure auth passes
        aggregate.simulateTimeout(); // Violate timeout invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        providedSessionId = "session-nav-fail";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated(); // Ensure auth passes
        aggregate.corruptNavigationState(); // Violate nav state invariant
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(providedSessionId, Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof TellerSessionEndedEvent, "Expected TellerSessionEndedEvent");
        
        TellerSessionEndedEvent endedEvent = (TellerSessionEndedEvent) event;
        assertEquals("teller.session.ended", endedEvent.type());
        assertEquals(providedSessionId, endedEvent.aggregateId());
        
        // Verify side effects on aggregate
        assertFalse(aggregate.isActive(), "Session should be terminated");
        assertFalse(aggregate.isAuthenticated(), "Sensitive state (auth) should be cleared");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain errors are modeled as Exceptions in this pattern (IllegalStateException for invariant violations)
        assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException for invariant violation, got: " + caughtException.getClass().getSimpleName());
        
        // Verify no events were emitted
        assertNull(resultEvents, "No events should be emitted when command is rejected");
    }
}
