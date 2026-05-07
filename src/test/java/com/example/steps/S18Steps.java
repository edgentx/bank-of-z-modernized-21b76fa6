package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // This step contextually prepares the command builder or variables.
        // Implementation detail: We construct the command in the 'When' step or a builder.
        // For this step definition style, we assume the command construction uses valid defaults in the When block,
        // or we store the valid ID. Let's assume the command is built in the @When block.
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command if not specified otherwise by context
        if (command == null) {
            command = new StartSessionCmd("session-123", "teller-1", "terminal-1", true);
        }
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Prepare command with authenticated = false
        command = new StartSessionCmd("session-auth-fail", "teller-1", "terminal-1", false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate time passing: Set last activity to 20 minutes ago
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        
        // Use a valid authenticated command, the rejection should come from the aggregate state
        command = new StartSessionCmd("session-timeout", "teller-1", "terminal-1", true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Set state to something other than IDLE (e.g. ALREADY_IN_TRANSACTION)
        aggregate.setNavigationState("IN_TRANSACTION");
        
        command = new StartSessionCmd("session-nav-fail", "teller-1", "terminal-1", true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        // Check message to distinguish specific errors if necessary, though not strictly required by Gherkin
        String message = capturedException.getMessage();
        System.out.println("Caught expected error: " + message);
    }
}
