package com.example.steps;

import com.example.domain.navigation.model.EndSessionCmd;
import com.example.domain.navigation.model.SessionEndedEvent;
import com.example.domain.navigation.model.TellerSessionAggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        Instant now = Instant.now();
        // Simulate an active, authenticated session
        aggregate = new TellerSessionAggregate(sessionId, "teller-1", true, true, now, now);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        // Session exists but teller is NOT authenticated
        aggregate = new TellerSessionAggregate(sessionId, "teller-1", false, false, Instant.now(), Instant.now());
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        Instant longAgo = Instant.now().minusSeconds(3600); // 1 hour ago
        // Session is active but timestamp is old (simulating inactivity)
        aggregate = new TellerSessionAggregate(sessionId, "teller-1", true, true, longAgo, longAgo);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-nav-error";
        // Session is inactive (navigation state violation for command execution)
        aggregate = new TellerSessionAggregate(sessionId, "teller-1", true, false, Instant.now(), Instant.now());
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by the aggregate setup in Given steps
        // We ensure the command targets the existing aggregate ID
        if (aggregate != null) {
            command = new EndSessionCmd(aggregate.id(), Instant.now());
        }
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        // Retrieve the aggregate to ensure we are testing the state defined in Given steps
        aggregate = repository.findById(aggregate.id()).orElseThrow();
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        assertEquals("session.ended", resultEvents.get(0).type());
        assertFalse(aggregate.isActive(), "Session should be terminated");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
