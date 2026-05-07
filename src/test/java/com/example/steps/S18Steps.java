package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
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
    private Exception caughtException;

    // Helper to create a valid aggregate
    private TellerSessionAggregate createValidAggregate() {
        return new TellerSessionAggregate("session-123");
    }

    // Helper to create a valid command
    private StartSessionCmd createValidCommand() {
        return new StartSessionCmd(
            "session-123",
            "teller-101",
            "terminal-T1",
            true, // authenticated
            Instant.now(),
            "HOME"
        );
    }

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled by createValidCommand in the When step
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled by createValidCommand in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(createValidCommand());
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    // Scenario 2: Not Authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = createValidAggregate();
        // We use a modified command for this specific scenario
        caughtException = null;
        resultEvents = null;
    }

    // Reusing the When step from above

    // Overriding the When for this specific scenario context manually in step def logic
    @When("the StartSessionCmd command is executed on unauthenticated context")
    public void theStartSessionCmdCommandIsExecutedUnauthenticated() {
        StartSessionCmd invalidCmd = new StartSessionCmd(
            "session-123",
            "teller-101",
            "terminal-T1",
            false, // NOT authenticated
            Instant.now(),
            "HOME"
        );
        try {
            resultEvents = aggregate.execute(invalidCmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException.getMessage().contains("authenticated"));
    }

    // Scenario 3: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = createValidAggregate();
        caughtException = null;
        resultEvents = null;
    }

    @When("the StartSessionCmd command is executed with stale activity")
    public void theStartSessionCmdCommandIsExecutedWithStaleActivity() {
        // Simulate a command with an old activity timestamp (older than 15 minutes)
        Instant staleTime = Instant.now().minusSeconds(1000); // 16 mins ago
        StartSessionCmd invalidCmd = new StartSessionCmd(
            "session-123",
            "teller-101",
            "terminal-T1",
            true,
            staleTime,
            "HOME"
        );
        try {
            resultEvents = aggregate.execute(invalidCmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    // Scenario 4: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = createValidAggregate();
        caughtException = null;
        resultEvents = null;
    }

    @When("the StartSessionCmd command is executed with invalid navigation")
    public void theStartSessionCmdCommandIsExecutedWithInvalidNavigation() {
        StartSessionCmd invalidCmd = new StartSessionCmd(
            "session-123",
            "teller-101",
            "terminal-T1",
            true,
            Instant.now(),
            "" // Empty navigation context
        );
        try {
            resultEvents = aggregate.execute(invalidCmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }
}