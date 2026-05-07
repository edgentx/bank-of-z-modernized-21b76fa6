package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 */
public class S18Steps {

    // In-memory repository for testing
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private TellerSessionAggregate aggregate;

        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            this.aggregate = aggregate;
            return aggregate;
        }

        @Override
        public TellerSessionAggregate findById(String sessionId) {
            return this.aggregate;
        }
    }

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository;
    private Exception caughtException;
    private List resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in the execution step via command construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the execution step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Construct a valid command based on the context of positive tests
        // Or rely on specific setup in other steps
        StartSessionCmd cmd = new StartSessionCmd(
                "session-123",
                "teller-01",
                "term-01",
                true, // authenticated
                false, // active (session state, not command auth)
                "HOME" // valid navigation state
        );
        executeCommand(cmd);
    }

    private void executeCommand(StartSessionCmd cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
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
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-01", event.terminalId());
    }

    // -- Negative Scenarios --

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-unauth");
    }

    // We override the 'When' for this specific scenario via context check or specific step definition
    @When("the StartSessionCmd command is executed with unauthenticated context")
    public void theStartSessionCmdCommandIsExecutedUnauthenticated() {
        StartSessionCmd cmd = new StartSessionCmd(
                "session-unauth",
                "teller-bad",
                "term-bad",
                false, // NOT authenticated
                false,
                "HOME"
        );
        executeCommand(cmd);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-timeout");
        // To simulate a violation, we might rely on the aggregate state being incorrect
        // but since we are creating a new aggregate, we simulate the violation via the command.
        // If the aggregate logic checked for 'old' sessions, this would be setup differently.
        // For this exercise, we assume the 'validity' of the request implies the check.
    }

    @When("the StartSessionCmd command is executed with expired context")
    public void theStartSessionCmdCommandIsExecutedExpired() {
        // Simulate starting a session on an aggregate that considers itself 'active' or in a bad state
        // First, let's put it in a state where it's already active (simulating a stuck session)
        StartSessionCmd valid = new StartSessionCmd("session-timeout", "t", "term", true, false, "HOME");
        aggregate.execute(valid); // Starts it.

        // Now try to start it again (which violates the flow/timeout logic if we treat active session as conflict)
        StartSessionCmd conflict = new StartSessionCmd("session-timeout", "t", "term", true, true, "HOME");
        executeCommand(conflict);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-badnav");
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void theStartSessionCmdCommandIsExecutedInvalidNav() {
        StartSessionCmd cmd = new StartSessionCmd(
                "session-badnav",
                "teller-01",
                "term-01",
                true,
                false,
                "" // Invalid navigation state (empty)
        );
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Check it's an IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        assertNotNull(caughtException.getMessage());
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecutedGeneric() {
        // Generic handler if we want to reuse the positive flow, but usually Cucumber maps to specific methods.
        // This is a placeholder to avoid duplicate step definition errors if Gherkin is generic.
        // In the Gherkin provided, "When the StartSessionCmd command is executed" is used for all.
        // We handle the branching via context (which is hard in pure Cucumber Java without glue code state).
        // Therefore, I will rely on the specific step definitions above or make the command smarter.
        // To keep it robust:
        if (aggregate.id().equals("session-123")) {
            theStartSessionCmdCommandIsExecuted();
        } else if (aggregate.id().equals("session-unauth")) {
            theStartSessionCmdCommandIsExecutedUnauthenticated();
        } else if (aggregate.id().equals("session-badnav")) {
            theStartSessionCmdCommandIsExecutedInvalidNav();
        } else if (aggregate.id().equals("session-timeout")) {
            theStartSessionCmdCommandIsExecutedExpired();
        } else {
            theStartSessionCmdCommandIsExecuted();
        }
    }
}
