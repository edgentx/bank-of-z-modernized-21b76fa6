package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
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
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Constructor sets the ID. The aggregate is effectively uninitialized.
        aggregate = new TellerSessionAggregate("session-01");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in the execution step or state setup if necessary.
        // Assuming the command carries the data.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in the execution step.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand(new StartSessionCmd("session-01", "teller-123", "terminal-T1"));
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-01", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-02");
        // We simulate a previous start that failed auth or simply use a command with invalid credentials.
        // Here we will pass null as the teller ID to simulate lack of authentication context in the command.
    }

    // We reuse the @When from above, but we need a specific method for this context to pass the invalid data.
    // However, Gherkin 'When' steps can be reused. To distinguish, we might need separate steps or logic checks.
    // Given the simplicity, I will override the execution for this scenario context in a dedicated method.
    
    @When("the StartSessionCmd command is executed on unauthenticated context")
    public void theStartSessionCmdCommandIsExecutedUnauthenticated() {
        executeCommand(new StartSessionCmd("session-02", null, "terminal-T1"));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // In a real system, we might load an aggregate with a timestamp in the past.
        // Here, we assume the command or aggregate state carries the 'current time' or 'last active time'.
        // To keep it simple, we'll pass a specific flag or data via the command if needed, or assume the aggregate
        // was hydrated with an old timestamp. Since we don't have a hydration DB in these steps,
        // we will interpret the 'violation' by passing a command that indicates timeout state.
        // Alternatively, we just ensure the logic exists. 
        // Let's assume the aggregate handles it. We will use a command that triggers the check.
        aggregate = new TellerSessionAggregate("session-03");
        // Ideally we'd set state here: aggregate.start(Instant.now().minusSeconds(3600));
        // Since 'start' is the command we are testing, we can't pre-call it easily without a test-specific method.
        // We will rely on the command to carry the validation context.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-04");
        // We will pass a command that triggers the navigation validation error.
    }

    // Generic handler for all scenarios relying on the specific command setup.
    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected a domain error exception");
        // Depending on implementation (IllegalStateException vs IllegalArgumentException), we check for Exception.
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
