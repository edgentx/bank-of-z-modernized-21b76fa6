package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Step Definitions for S-20: EndSessionCmd.
 * S-20: Implement EndSessionCmd on TellerSession
 */
public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession session;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        session = new TellerSession("session-123");
        session.markAuthenticated(); // Ensure valid state
        repository.save(session);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled by the aggregate instance in memory
        assertNotNull(session.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        session = new TellerSession("session-unauth");
        // Defaults to authenticated=false, which violates the rule for ending
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        session = new TellerSession("session-timeout");
        session.markAuthenticated();
        // Set last activity to 16 minutes ago (Timeout is 15)
        session.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(16)));
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        session = new TellerSession("session-invalid-ctx");
        session.markAuthenticated();
        session.setContextValid(false);
        repository.save(session);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        EndSessionCmd cmd = new EndSessionCmd(session.id());
        try {
            // Reload from repo to ensure we are testing persistence/state integrity if needed,
            // though here we act on the instance directly for unit-level aggregate logic.
            resultEvents = session.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        assertEquals("session.ended", resultEvents.get(0).type());
        assertEquals(session.id(), resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
