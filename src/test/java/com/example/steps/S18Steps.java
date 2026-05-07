package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private Throwable thrownException;
    private Iterable<DomainEvent> resultEvents;

    // --- Scenarios Setup ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate the invariant violation: teller is not authenticated
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate the invariant violation: session is timed out
        aggregate.markSessionTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate the invariant violation: navigation state is invalid
        aggregate.markNavigationInvalid();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // tellerId is part of the command construction in the 'When' step, 
        // but we can assert validity here if we were storing state.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Similar to tellerId, handled in command construction.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Validate invariants BEFORE execution if the scenario implies a check
            // In CQRS, invariants are usually checked INSIDE execute.
            // For S-18, execute() checks generic state.
            // To satisfy the specific 'violates' scenarios in Gherkin which often imply
            // that the aggregate state is the source of truth:
            // We will execute the command. The aggregate's execute method should throw.
            
            // Note: The simple aggregate implementation checks state internally.
            // If we want to test the specific 'auth' flag, the aggregate needs to look at it.
            // We added checkInvariantsForStart logic to the aggregate to satisfy the specific 'violates' logic.
            
            // However, standard AggregateRoot.execute pattern just runs the command.
            // Let's trigger the command. The aggregate must be pre-set to fail.
            
            // But wait, `execute(Command)` is the entry point. 
            // We manually trigger the logic inside the aggregate for the 'violates' cases
            // or we rely on the `execute` method to call the checks.
            
            // Let's assume standard execution:
            cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1");
            
            // To make the specific scenarios work (Auth fail, Nav fail, Timeout),
            // we need to ensure the aggregate checks these.
            // Since the command doesn't carry 'authentication' status, the aggregate must know.
            // The helper methods in the aggregate set the state.
            // We will modify the aggregate logic to check these flags if we were implementing the Guard clauses.
            // Based on the prompt, we are implementing the domain code.
            // The `startSession` method checks `active`. 
            // To handle the specific violations:
            // 1. Auth: `aggregate.setAuthenticated(false)` is called in Given.
            //    `startSession` must check this.
            // 2. Timeout: `aggregate.markSessionTimedOut()` is called.
            //    `startSession` must check this.
            // 3. Nav: `aggregate.markNavigationInvalid()` is called.
            //    `startSession` must check this.

            // Since I am writing the domain code AND the steps, I will ensure the domain code performs these checks.
            // I've added `checkInvariantsForStart` to the aggregate. 
            // I will invoke it explicitly or ensure `execute` does.
            // Ideally, `execute` calls it. I will assume `execute` calls `checkInvariantsForStart` implicitly via logic in `startSession`.
            // But to keep `startSession` clean for the success case, and force failure in `execute` for violations:
            // I'll wrap the execution in a try-catch that validates the specific domain error.
            
            // Strategy: The aggregate `execute` method calls `checkInvariantsForStart`.
            // Actually, the aggregate `startSession` method should throw.
            // I will modify `startSession` to call the check.
            // (Modifying `TellerSessionAggregate.startSession` in thought process to include `checkInvariantsForStart();` as first line)
            
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events but got null");
        assertTrue(resultEvents.iterator().hasNext(), "Expected at least one event");
        DomainEvent event = resultEvents.iterator().next();
        assertEquals("session.started", event.type());
        assertTrue(event instanceof SessionStartedEvent);
        SessionStartedEvent sse = (SessionStartedEvent) event;
        assertEquals("session-123", sse.aggregateId()); // Matches setup
        assertEquals("teller-1", sse.tellerId());
        assertEquals("term-1", sse.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, 
            "Expected domain error, got " + thrownException.getClass().getSimpleName());
    }
}
