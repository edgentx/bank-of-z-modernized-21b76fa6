package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Assuming fresh aggregate is valid for start session unless specific state violates invariants
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Step placeholder: Data is passed via the Command object in the 'When' step
        // We assume valid inputs like "Teller-123" are used.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Step placeholder: Data is passed via the Command object
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        theStartSessionCmdCommandIsExecutedWith("Teller-123", "TERM-01");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set up the aggregate in a way that it believes it cannot be authenticated
        // or marks the teller as unauthenticated for this specific logic check.
        // For this implementation, we use a marker method to simulate the violation condition.
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a stale timestamp or condition that implies a timeout has occurred pre-start
        // (Edge case: strictly for 'StartSession' invariants, perhaps 'previous attempt timed out')
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a mismatch in navigation context (e.g. terminal claims Main Menu, but system says Login)
        aggregate.markNavigationMismatch();
    }

    @When("the StartSessionCmd command is executed with teller {string} and terminal {string}")
    public void theStartSessionCmdCommandIsExecutedWith(String tellerId, String terminalId) {
        StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Standard domain exceptions (IllegalStateException, IllegalArgumentException) are acceptable domain errors
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
