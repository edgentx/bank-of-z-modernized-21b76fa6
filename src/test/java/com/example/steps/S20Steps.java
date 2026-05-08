package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private Exception caughtException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Create a session that is active and authenticated
        aggregate = new TellerSession("session-123", "teller-001");
        // Simulate a previous login command to establish valid state
        aggregate.execute(new Command() { // Dummy Login cmd equivalent
            public String tellerId() { return "teller-001"; }
        });
        aggregate.clearEvents();
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicit in the aggregate ID used in the constructor
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create a session without a teller (null/unauthenticated)
        aggregate = new TellerSession("session-unauth", null);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Create a session that is explicitly timed out
        aggregate = new TellerSession("session-timeout", "teller-002");
        aggregate.markAsTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // Create a session in an invalid navigation state
        aggregate = new TellerSession("session-nav-error", "teller-003");
        aggregate.markNavigationStateInvalid();
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        assertTrue(resultEvents.iterator().next() instanceof SessionEndedEvent);
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect an IllegalStateException or IllegalArgumentException based on invariants
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
