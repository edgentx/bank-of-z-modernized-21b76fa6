package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.*;

public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private DomainEvent resultEvent;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context implied for the next command execution
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context implied for the next command execution
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand(new StartSessionCmd("session-123", "teller-101", "term-T1", Duration.ofMinutes(30), Instant.now()));
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull("Expected no exception, but got: " + thrownException, thrownException);
        assertNotNull("Expected an event to be emitted", resultEvent);
        assertEquals("session.started", resultEvent.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // We simulate the violation by attempting to start a session without credentials
        // In this specific aggregate design, the command must contain the token.
        // We will pass an invalid token to trigger the failure.
    }

    // Note: We override the When step for this scenario via specific context handling
    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        // Execute with a null/empty auth token to violate the invariant
        executeCommand(new StartSessionCmd("session-401", "teller-101", "term-T1", Duration.ofMinutes(30), Instant.now(), null));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void theStartSessionCmdCommandIsExecutedWithInvalidTimeout() {
        // Violation: Timeout is 0 or negative
        executeCommand(new StartSessionCmd("session-timeout", "teller-101", "term-T1", Duration.ZERO, Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav");
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void theStartSessionCmdCommandIsExecutedWithInvalidNavState() {
        // Violation: Nav state is null
        executeCommand(new StartSessionCmd("session-nav", "teller-101", "term-T1", Duration.ofMinutes(30), Instant.now(), null, null));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull("Expected a domain error exception", thrownException);
        assertTrue("Expected IllegalStateException or IllegalArgumentException", 
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    private void executeCommand(Command cmd) {
        try {
            // 1. Load Aggregate (or create new for this root)
            // Since this is a Start command, we might be creating the aggregate state in the repository 
            // or acting on a new instance. For BDD, we act on the instance field.
            
            // 2. Execute
            List<DomainEvent> events = aggregate.execute(cmd);
            
            // 3. Capture Result
            if (!events.isEmpty()) {
                resultEvent = events.get(0);
            }
            
            // 4. Apply to state (In a real repo, this happens during load)
            // Here we manually apply if we want to test state changes, but for 'event emitted', checking return is enough.
            
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}
