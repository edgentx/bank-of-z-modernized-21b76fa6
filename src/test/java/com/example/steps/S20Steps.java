package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSession session;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Constants matching the Aggregate
    private static final String STATE_IDLE = "IDLE";
    private static final String STATE_TRANSACTION = "TRANSACTION";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        session = new TellerSession("session-123");
        // Setup a valid, authenticated, active session
        session.markAuthenticated("teller-01");
        session.setLastActivityAt(Instant.now());
        session.setNavigationState(STATE_IDLE);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        session = new TellerSession("session-unauth");
        // Do NOT mark authenticated
        session.setNavigationState(STATE_IDLE);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        session = new TellerSession("session-timeout");
        session.markAuthenticated("teller-02");
        // Set activity to 20 minutes ago (assuming 15 min timeout)
        session.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        session.setNavigationState(STATE_IDLE);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        session = new TellerSession("session-busy");
        session.markAuthenticated("teller-03");
        session.setLastActivityAt(Instant.now());
        // Put in a state that is not IDLE
        session.setNavigationState(STATE_TRANSACTION);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate creation in Given steps
        // No-op unless we need to construct the Cmd explicitly with a different ID
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(session.id());
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals("session-123", event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
