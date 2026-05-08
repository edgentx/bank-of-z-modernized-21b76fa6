package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String validTellerId = "TELLER-123";
    private String validTerminalId = "TERM-A";
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Value initialized in field, used in execution step
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Value initialized in field, used in execution step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION-2");
        validTellerId = null; // Violate auth
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-3");
        aggregate.markAsTimedOut(); // Force state to TIMED_OUT to trigger invariant check
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("SESSION-4");
        // Manually push state to ACTIVE (simulating a concurrency issue or bad migration)
        // The execute() logic checks state == ACTIVE and throws error.
        // We cannot set state directly, so we must rely on the fact that the logic checks this.
        // However, since we start with NONE, we need to simulate a case where it's already active.
        // This is tricky without a repository load. We will simulate by hacking the aggregate if possible,
        // or relying on the logic in execute("State.ACTIVE -> throw").
        // Since aggregate state is private, let's assume the invariant is "Cannot start an already started session".
        // To test this, we'd need to start it twice. But the scenario implies the *Given* state is the violation.
        // Let's refine: We will create a NEW aggregate, but the command execution logic handles the "State.ACTIVE" check.
        // Since we cannot access package-private setters from steps easily without reflection, and the rules forbid changing visibility,
        // we will assume this scenario is covered by the logic: "State must be NONE to start".
        // Wait, the prompt asks me to use the specific violations.
        // Let's simulate the state being invalid via a custom aggregate setup if I could, but I can't.
        // Re-reading the aggregate logic: if (state == State.ACTIVE) throw ...
        // I will verify that running the command on an already active session fails.
        // So for "Given... violation", I will actually start the session successfully first.
        
        // Execute a valid start command to reach ACTIVE state
        StartSessionCmd firstCmd = new StartSessionCmd(aggregate.id(), "USER", "TERM");
        aggregate.execute(firstCmd);
        // Now aggregate is ACTIVE. Next execution will fail.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception but command succeeded");
        // The error can be IllegalStateException or IllegalArgumentException depending on the invariant.
        // Domain errors are often RuntimeExceptions.
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

}