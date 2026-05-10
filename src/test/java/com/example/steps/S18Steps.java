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
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private String validTellerId = "tell-100";
    private String validTerminalId = "term-200";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Valid ID set in field, used in When
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Valid ID set in field, used in When
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd("session-1", validTellerId, validTerminalId, true);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We pass false for authenticated in the When step via flag or scenario context
        // For this specific step, we'll use the When to control the command payload,
        // but technically we could set aggregate state if the rule was aggregate-owned.
        // The rule checks the command payload for authentication status.
    }

    // We need a specific When or context to handle the violation injection.
    // However, to keep the When generic, we can use a shared boolean flag or just modify the command construction logic
    // based on the scenario state. Here, let's modify the command logic in a specific When or overload the method.
    // But Cucumber matches method names uniquely. Let's use a thread-local or field to control the command.
    
    // Simpler approach for this demo: specific When steps or modifying the generic When's behavior based on aggregate state.
    // Since the generic When uses `validTellerId`, we need a way to pass `isAuthenticated=false`.
    // Let's assume the generic When builds a valid command. For violations, we might need a specific When or a flag.
    // However, the prompt maps specific violations to specific Givens. The When is shared "the StartSessionCmd command is executed".
    // I will check the aggregate state in the When block to determine the command payload?
    // No, the Aggregate is the receiver. The Command is the argument.
    // I will use a field to control the command payload.

    private boolean isAuthenticated = true;
    private String terminalIdOverride = null;
    private boolean forceInvalidContext = false;
    private boolean forceTimedOut = false;

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        isAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAsTimedOut(); // Setup aggregate state that the command execution will reject
        // Note: The StartSessionCmd logic checks aggregate state. 
        // If the aggregate is ALREADY timed out, we can't start.
        isAuthenticated = true; // This is valid
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.setInvalidContext(); // Setup state
        isAuthenticated = true;
    }

    // Re-implementing the When to handle the flags
    @When("the StartSessionCmd command is executed")
    public void executeStartSessionCmdWithFlags() {
        String termId = (terminalIdOverride != null) ? terminalIdOverride : validTerminalId;
        Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, termId, isAuthenticated);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception but none was thrown");
        // Check it's a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}