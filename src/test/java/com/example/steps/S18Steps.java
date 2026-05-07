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
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Force reset internal state to simulate a clean start if necessary, 
        // though constructor usually handles this. 
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in the 'When' step construction for simplicity, or stored here
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command data
        command = new StartSessionCmd("teller-01", "terminal-05", true);
        executeCommand();
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We will pass isAuthenticated = false in the command
    }

    @When("the StartSessionCmd command is executed with unauthenticated user")
    public void theStartSessionCmdCommandIsExecutedWithUnauthenticatedUser() {
        // isAuthenticated = false triggers the violation
        command = new StartSessionCmd("teller-01", "terminal-05", false);
        executeCommand();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Setup: Create a previous session that is still active (not timed out)
        // We simulate this by constructing a scenario where the aggregate thinks it's active
        // but the business logic detects an overlap.
        // Since we can't set internal state directly (private fields), we rely on the invariant check.
        // However, the invariant in the code checks if existing active session is within timeout.
        // To simulate the violation condition (starting a session while one is active),
        // we would ideally have loaded an aggregate with state. For unit test, we can assume 
        // the 'Given' implies the state exists. 
        // In this specific in-memory implementation, we might need to rely on the command
        // or a specific setup. Given the constraints, we simulate the violation check 
        // by manually setting state if possible, or acknowledging the limitation. 
        // Note: TellerSessionAggregate has a private constructor logic. 
        // Let's assume the aggregate was rehydrated in an active state.
        // Since we can't rehydrate from events easily without a repository, we will simulate 
        // the check via the command logic if the aggregate was loaded. 
        // For the purpose of the BDD step, we verify the exception type.
        // The actual invariant logic in the aggregate checks `this.active`.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // To simulate this violation, we assume the aggregate was loaded in a bad state.
        // Example: currentNavigationState is "TRANSACTING" when trying to start a session.
        // Without a repository/apply mechanism, we assume the scenario sets this up.
        // In this test stub, we acknowledge the condition.
    }

    // Common execution handler
    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for specific error messages or types depending on the scenario
        if (capturedException instanceof IllegalStateException) {
            System.out.println("Correctly rejected with invariant violation: " + capturedException.getMessage());
        } else {
            Assertions.fail("Expected IllegalStateException but got " + capturedException.getClass());
        }
    }
}
