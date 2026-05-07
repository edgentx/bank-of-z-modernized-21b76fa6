package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionEndedEvent;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20 (EndSessionCmd).
 */
public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private String tellerId = "teller-456";
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a pre-existing active session (since InitSessionCmd is not in scope)
        aggregate.initSession(tellerId);
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Initialize but mark as not authenticated
        aggregate.initSession(tellerId);
        aggregate.markUnauthenticated();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initSession(tellerId);
        aggregate.markExpired();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initSession(tellerId);
        // Put in a state where ending is invalid
        aggregate.markInvalidNavigationContext();
        repository.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            // Reload from repo to ensure we are testing persistence/reloading logic if needed,
            // or just use the instance if the repo holds references.
            // For InMemory, it's the same reference.
            aggregate = repository.findById(sessionId).orElseThrow();
            Command cmd = new EndSessionCmd(sessionId, tellerId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);

        TellerSessionEndedEvent event = (TellerSessionEndedEvent) resultEvents.get(0);
        assertEquals("teller.session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertFalse(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
