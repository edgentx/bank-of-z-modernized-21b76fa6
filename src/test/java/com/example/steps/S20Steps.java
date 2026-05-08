package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSession session;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        session = new TellerSession("session-123");
        session.markAuthenticated(); // Sets authenticated = true, active = true
        session.updateContext("MAIN_MENU"); // Sets context
        repo.save(session);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId "session-123" is implied from the aggregate created
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            session = repo.findById("session-123").orElseThrow();
            resultEvents = session.execute(new EndSessionCmd("session-123"));
            repo.save(session);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        session = new TellerSession("session-auth-fail");
        // Intentionally NOT calling markAuthenticated(). authenticated = false.
        session.setActive(true); // Assume session exists but is not authenticated (shouldn't happen in real flow, but tests invariant)
        repo.save(session);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        session = new TellerSession("session-timeout");
        session.markAuthenticated();
        // Set last activity to 20 minutes ago (Timeout is 15)
        session.setLastActivityAt(Instant.now().minusSeconds(20 * 60));
        repo.save(session);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        session = new TellerSession("session-nav-fail");
        session.markAuthenticated();
        session.setCurrentContext(null); // Null context while active is invalid per our check
        repo.save(session);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
