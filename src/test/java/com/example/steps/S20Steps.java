package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_TELLER_ID = "teller-42";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.configureAsValidSession(VALID_TELLER_ID);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is inherently part of the aggregate construction in this context
        // The command payload carries it for correlation
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID);
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + 
            (capturedException != null ? capturedException.getMessage() : ""));
        
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        
        var event = events.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Expected SessionEndedEvent");
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals(VALID_SESSION_ID, endedEvent.sessionId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.configureAsValidSession(VALID_TELLER_ID);
        aggregate.violateAuthentication(); // Flip the auth flag to false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.configureAsValidSession(VALID_TELLER_ID);
        aggregate.violateTimeout(); // Set timestamp to past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.configureAsValidSession(VALID_TELLER_ID);
        aggregate.violateNavigationState(); // Set state to TRANSACTION_IN_PROGRESS
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        
        // The prompt implies "domain error". In Java DDD with the Execute pattern,
        // this is often an IllegalStateException (invariant violation) or specific domain exception.
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof UnknownCommandException,
            "Expected domain error (IllegalStateException), but got: " + capturedException.getClass().getSimpleName()
        );
    }
}
