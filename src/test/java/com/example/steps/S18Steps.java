package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd
 */
@SpringBootTest
public class S18Steps {

    // In-memory state for the scenario
    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    private String validTellerId = "TELLER-001";
    private String validTerminalId = "TERM-01";
    
    // Context flags to simulate violations
    private boolean shouldFailAuth = false;
    private boolean shouldFailTimeout = false;
    private boolean shouldFailNav = false;

    // Result capture
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Reset flags
        shouldFailAuth = false;
        shouldFailTimeout = false;
        shouldFailNav = false;
        
        String sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = repository.create(sessionId);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Assume default is valid, just ensuring the variable is set
        assertNotNull(validTellerId);
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        assertNotNull(validTerminalId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aValidTellerSessionAggregate();
        shouldFailAuth = true;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aValidTellerSessionAggregate();
        shouldFailTimeout = true;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aValidTellerSessionAggregate();
        shouldFailNav = true;
    }

    // --- Whens ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                validTellerId,
                validTerminalId,
                !shouldFailAuth,     // authenticated
                shouldFailTimeout,   // timedOut
                !shouldFailNav       // navigationStateValid
            );
            
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Thens ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception but command succeeded");
        
        // Check for the specific error messages defined in the aggregate
        assertTrue(
            capturedException.getMessage().contains("authenticated") ||
            capturedException.getMessage().contains("timeout") ||
            capturedException.getMessage().contains("Navigation state")
        );
    }

    // --- Mocks ---

    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public void save(TellerSessionAggregate aggregate) {
            // No-op for in-memory test
        }

        @Override
        public TellerSessionAggregate create(String id) {
            return new TellerSessionAggregate(id);
        }

        @Override
        public TellerSessionAggregate findById(String id) {
            return null;
        }
    }
}
