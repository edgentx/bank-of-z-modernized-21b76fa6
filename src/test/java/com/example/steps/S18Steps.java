package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Throwable caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();

    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            return aggregate;
        }
        @Override
        public TellerSessionAggregate findById(String id) {
            return null;
        }
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Pre-condition for valid start: Authenticated
        aggregate.markAuthenticated();
        // Pre-condition for valid start: Correct Nav Context
        aggregate.setNavigationContext("HOME");
        // Pre-condition for valid start: Active (recent) timestamp
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        // Intentionally not marking authenticated
        aggregate.setNavigationContext("HOME");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.setNavigationContext("HOME");
        // Set time to well beyond the 30 minute timeout defined in the aggregate
        aggregate.setLastActivityAt(Instant.now().minus(Duration.of(1, ChronoUnit.HOURS)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Set wrong navigation context
        aggregate.setNavigationContext("INVALID_STATE");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context implied, parameterized in When
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context implied, parameterized in When
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
