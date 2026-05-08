package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.command.StartSessionCmd;
import com.example.domain.tellersession.event.SessionStartedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to create a valid basic command for modification in tests
    private StartSessionCmd createValidCommand() {
        return new StartSessionCmd("session-123", "teller-01", "term-05", true, Instant.now());
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Ensure clean state
        assertNotNull(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup handled in 'When' step via createValidCommand()
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup handled in 'When' step via createValidCommand()
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-123");
        // Logic check: The aggregate doesn't hold 'isAuthenticated' state until after command execution,
        // OR we modify the command to carry false auth. The prompt implies checking invariants.
        // Since the invariant is checked on the Command payload in this design (auth is passed in):
        // We will handle this by passing 'authenticated=false' in the When step.
        // However, if the aggregate held state preventing this, we would set it here.
        // aggregate.markUnauthenticated(); // If relying on aggregate state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-123");
        // To simulate a violation of "must timeout" (i.e., we are trying to start an already timed out session?)
        // Actually, the prompt says "Sessions must timeout after a configured period of inactivity".
        // This usually applies to *extending* a session or a command checking validity.
        // Here, we interpret the violation as attempting to start a session based on a timestamp that implies staleness,
        // OR simulating that the system checks session validity.
        // Let's assume the aggregate was previously active and now timed out, preventing a restart or invalidating state.
        // For this BDD, we'll mark the aggregate as timed out if the logic requires checking existing state.
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-123");
        // Force the navigation state to something other than INIT to violate the context for starting a session
        aggregate.corruptNavigationState("INVALID_STATE");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = createValidCommand();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with authentication violation")
    public void theStartSessionCmdCommandIsExecutedUnauthenticated() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-01", "term-05", false, Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Mapping specific When steps to specific scenarios based on context description
    @When("the StartSessionCmd command is executed with timeout violation")
    public void theStartSessionCmdCommandIsExecutedWithTimeout() {
        // We rely on the aggregate state setup in the Given step
        // To trigger the specific logic, we might need a specific command or the aggregate logic checks the state.
        // Given our domain logic checks `isActive` or `navigationState`, we use the standard command execution
        // and let the aggregate throw.
        try {
            // Re-using valid command, expecting aggregate state to cause failure
            StartSessionCmd cmd = createValidCommand(); 
            // To simulate timeout check, we might need to pass a 'old' timestamp or rely on aggregate.markTimedOut()
            // The domain logic provided checks `isActive = false` (if we added that check) or navigation state.
            // Based on the provided code, `isActive` is set to true. Let's assume the logic for timeout
            // checks `lastActivityAt` vs `sessionTimeoutAt` if the session exists.
            // Since this is a Start command, it usually implies new session.
            // However, to satisfy the BDD "rejected" requirement for timeout, we check the state.
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with navigation violation")
    public void theStartSessionCmdCommandIsExecutedWithNavViolation() {
        try {
            // Aggregate state was corrupted in Given step
            StartSessionCmd cmd = createValidCommand();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should be thrown for domain violation");
        // We check for IllegalStateException or IllegalArgumentException as domain errors
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

}
