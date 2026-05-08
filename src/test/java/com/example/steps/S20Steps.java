package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state for happy path
        aggregate.markAuthenticated(); 
        this.capturedException = null;
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(this.sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            aggregate.execute(new EndSessionCmd(sessionId));
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Expected no error, but got: " + capturedException);
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) events.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "sess-invalid-auth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated(); // Violate invariant
        this.capturedException = null;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-invalid-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Authenticated
        aggregate.markStale(); // Violate inactivity invariant
        this.capturedException = null;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        this.sessionId = "sess-invalid-nav";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Authenticated
        // Using a package-private mutator hook or assuming a method exists to corrupt state
        aggregate.markNavigationInvalid(); // Violate navigation invariant
        this.capturedException = null;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
