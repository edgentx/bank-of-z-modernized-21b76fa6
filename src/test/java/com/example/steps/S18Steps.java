package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Note: Using direct instantiation for steps to keep it decoupled from Spring context complexity unless necessary.
// However, Cucumber in Spring usually requires a configuration class or component scanning.
// We will use a simple mock repository pattern here.

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate successful authentication state
        aggregate.markAuthenticated(true);
        // Ensure not timed out
        aggregate.setLastActivityAt(Instant.now());
        // Ensure clean terminal state
        aggregate.setTerminalId(null); // No conflict expected
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context stored in scenario state, usually via a DTO, but here we just ensure 
        // the command we build later has this. 
        // For simplicity in this specific step, we do nothing but acknowledge the context,
        // as the command is built in the 'When' step or a shared state object.
        // If strict mapping is needed, we'd store 'currentTellerId' in a field.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(false); // The violation
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        // Set time far in the past to trigger timeout logic
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        // Simulate a mismatch: The aggregate thinks it is at TERMINAL-A, but command is for TERMINAL-B
        aggregate.setTerminalId("TERMINAL-A");
    }

    // --- When Steps ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // We assume valid IDs for the successful path and specific ones for violations if needed.
        // Since violations are pre-set in the aggregate state above, generic valid IDs work for the command payload itself,
        // except for the Navigation state test which specifically checks terminal ID mismatch.
        
        String tId = "teller-01";
        String termId = "TERMINAL-B"; // Matches the violation scenario setup
        
        // If we are in the generic "Valid" scenario, we should align the aggregate state to accept this.
        // If the aggregate was created in the "Valid" Given step, terminalId is null, so it accepts anything.
        
        Command cmd = new StartSessionCmd(aggregate.id(), tId, termId);
        
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Then Steps ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-01", event.tellerId());
        assertEquals("TERMINAL-B", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In this domain, we use RuntimeException or IllegalStateException for domain errors
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // --- Mocks ---
    
    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public void save(TellerSessionAggregate aggregate) {
            // No-op for unit test scope
        }
        @Override
        public TellerSessionAggregate load(String sessionId) {
            return null; // Not needed for this command flow
        }
    }
}