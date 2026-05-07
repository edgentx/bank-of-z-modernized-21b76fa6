package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions for S-18: TellerSession StartSessionCmd.
 */
@SpringBootTest
public class S18Steps {

    // Test Context
    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    // Inputs
    private String currentTellerId;
    private String currentTerminalId;
    
    // Outputs
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-test-123";
        aggregate = repository.loadOrCreate(sessionId);
        // Default valid state setup
        aggregate.markAuthenticated(true); // Assume pre-authenticated for success case
        aggregate.setOperationalContext("IDLE");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.currentTellerId = "TELLER-001";
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.currentTerminalId = "TERM-A01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-auth-fail";
        aggregate = repository.loadOrCreate(sessionId);
        aggregate.markAuthenticated(false); // Violation
        aggregate.setOperationalContext("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = repository.loadOrCreate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.setOperationalContext("IDLE");
        // Set last activity to 2 hours ago (Violation)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(7200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-nav-error";
        aggregate = repository.loadOrCreate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.setOperationalContext("TRANSACTION_IN_PROGRESS"); // Violation: Should be IDLE
    }

    // --- When Steps ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId);
            resultingEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Then Steps ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(aggregate.id(), startedEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Exception should have been thrown");
        // We check for IllegalStateException or RuntimeException indicating domain rule violation
        assertTrue(thrownException instanceof IllegalStateException, 
                   "Exception should be IllegalStateException");
        
        // Verify event list is null or empty because command failed
        assertTrue(resultingEvents == null || resultingEvents.isEmpty(), 
                   "No events should be emitted on command rejection");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError(String expectedMessagePart) {
        theCommandIsRejectedWithADomainError();
        assertTrue(thrownException.getMessage().contains(expectedMessagePart),
                   "Exception message should contain: " + expectedMessagePart);
    }
}
