package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession session;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Scenarios Setup ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        UUID id = UUID.randomUUID();
        session = new TellerSession(id, "teller-123");
        session.markAuthenticated(); // Make it valid and active
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        UUID id = UUID.randomUUID();
        session = new TellerSession(id, "teller-123");
        // Do NOT mark authenticated - violates invariant
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        UUID id = UUID.randomUUID();
        session = new TellerSession(id, "teller-123");
        session.markAuthenticated();
        session.markTimedOut(); // Set last activity to the past
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        UUID id = UUID.randomUUID();
        session = new TellerSession(id, "teller-123");
        session.markAuthenticated();
        session.setNavigationState(""); // Invalid state
        repository.save(session);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate creation in Given steps
        assertNotNull(session.id());
    }

    // --- Action ---

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(UUID.fromString(session.id()));
            resultEvents = session.execute(cmd);
            repository.save(session); // Save updated state
        } catch (IllegalStateException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
