package com.example.steps;

import com.example.domain.shared.DomainEvent;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    private StartSessionCmd validCmd;

    // Scenario 1: Successfully execute
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        validCmd = new StartSessionCmd("session-1", "teller-100", "term-200", true, "DEFAULT");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in the setup above, but we ensure the command has it
        assertNotNull(validCmd.tellerId());
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the setup above
        assertNotNull(validCmd.terminalId());
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(validCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    // Scenario 2: Rejected - Auth
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Create command with isAuthenticated = false
        validCmd = new StartSessionCmd("session-2", "teller-100", "term-200", false, "DEFAULT");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario 3: Rejected - Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // Simulate an active session that hasn't been used in a long time
        aggregate.forceActive();
        // Set last activity to 2 hours ago (configured timeout is 30 mins)
        aggregate.setLastActivityAt(Instant.now().minus(2, java.time.temporal.ChronoUnit.HOURS));
        
        // Attempt to start/refresh (which triggers check)
        validCmd = new StartSessionCmd("session-3", "teller-100", "term-200", true, "DEFAULT");
    }

    // Scenario 4: Rejected - Nav State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-4");
        // Pass invalid context state
        validCmd = new StartSessionCmd("session-4", "teller-100", "term-200", true, "INVALID");
    }

    // In-Memory Stub for Repository (if needed for persistence steps, currently using direct aggregate)
    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public void save(TellerSessionAggregate aggregate) {
            // No-op for this step definition scope
        }
        @Override
        public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.empty();
        }
    }
}
