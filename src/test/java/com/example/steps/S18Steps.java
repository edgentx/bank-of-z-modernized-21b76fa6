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

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd.StartSessionCmdBuilder cmdBuilder;
    private Exception capturedException;
    private DomainEvent resultEvent;

    // Helper to build a valid baseline command
    private StartSessionCmd.StartSessionCmdBuilder baseCmd() {
        return StartSessionCmd.builder() // Assuming lombok builder or manual factory. If record, just new StartSessionCmd(...)
               // Manual construction since record doesn't have builder by default in standard java
               ;
    }

    private StartSessionCmd createValidCmd() {
        return new StartSessionCmd(
            "session-123",
            "teller-01",
            "terminal-05",
            true,  // authenticated
            false, // timedOut
            ""     // navState (empty for valid start)
        );
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in context of the command creation in the 'When' step or via a builder pattern context
        // For simplicity, we assume the command created in 'When' uses valid IDs unless specified otherwise by scenario context
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Determine command context based on previous Given steps (not explicitly implemented with state flags here for brevity, relying on scenario flow)
        // Default to valid if not marked as violating invariants
        if (cmdBuilder == null) {
            // Default valid command
            executeCmd(createValidCmd());
        } else {
            // This branch would be used if we implemented a complex builder context in Given steps.
            // For this implementation, we handle the negative cases directly in their Given steps.
            executeCmd(createValidCmd());
        }
    }

    private void executeCmd(Command cmd) {
        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvent, "Expected an event to be emitted");
        assertTrue(resultEvent instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        assertEquals("session.started", resultEvent.type());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error exception");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-violate-auth");
        // Override the default valid command with an invalid one for this flow
        cmdBuilder = null; // Reset builder
        // We will use a flag or specific command execution in the next step.
        // However, to fit the 'When the StartSessionCmd command is executed' step reuse,
        // we can set a state variable, or simpler, create a specific command execution path.
        // Let's execute the specific negative command immediately here or in a modified When.
        // Best Practice for Cucumber: Set context, execute generic When.
        
        // Since 'When' is shared, we need a way to tell it to use the 'bad' command.
        // We'll just execute it here for clarity, but to follow strict Gherkin, we'd use a context holder.
        // For this output, let's assume the scenario flows sequentially.
        
        // Actually, let's look at the specific violation methods.
        // I will modify the 'When' logic to look for a specific pending command, or just execute here if the pattern allows.
        // To keep it clean and working with the shared 'When':
        this.currentCmd = new StartSessionCmd("s", "t", "term", false, false, "");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-violate-timeout");
        this.currentCmd = new StartSessionCmd("s", "t", "term", true, true, "");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        aggregate = new TellerSessionAggregate("session-violate-nav");
        this.currentCmd = new StartSessionCmd("s", "t", "term", true, false, "INVALID_CONTEXT");
    }

    // Context field to hold the specific command for the negative scenarios
    private StartSessionCmd currentCmd;

    // Refining the 'When' to handle the context
    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecutedContextual() {
        Command cmdToExecute = (currentCmd != null) ? currentCmd : createValidCmd();
        executeCmd(cmdToExecute);
    }
}
