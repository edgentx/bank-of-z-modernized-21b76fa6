package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Generate a valid UUID for the session
        String sessionId = java.util.UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Valid Teller ID is a non-blank string
        // We will construct the command in the 'When' step, or store params here.
        // For simplicity, we'll assume params are set in context for the command creation.
        // Let's store the tellerId in a helper var if needed, or just use literals in command.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Valid Terminal ID is a non-blank string
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Assuming valid IDs are "teller-123" and "term-ABC"
            // If we were testing violations, these would be invalid.
            // This method handles the 'Happy Path' and constructs valid defaults if not set by negative Given steps.
            String teller = "teller-123";
            String terminal = "term-ABC";
            
            command = new StartSessionCmd(aggregate.id(), teller, terminal);
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        assertEquals("teller-123", event.tellerId());
        assertEquals("term-ABC", event.terminalId());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(java.util.UUID.randomUUID().toString());
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        try {
            // Passing blank/null tellerId to simulate violation
            command = new StartSessionCmd(aggregate.id(), "", "term-ABC");
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatIsTimedOut() {
        aggregate = new TellerSessionAggregate(java.util.UUID.randomUUID().toString());
        // Simulate an aggregate that is already in a TIMED_OUT state
        aggregate.markAsTimedOut();
    }

    @When("the StartSessionCmd command is executed on timed out session")
    public void theStartSessionCmdCommandIsExecutedOnTimedOutSession() {
        try {
            command = new StartSessionCmd(aggregate.id(), "teller-123", "term-ABC");
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateWithBadNavState() {
        aggregate = new TellerSessionAggregate(java.util.UUID.randomUUID().toString());
    }

    @When("the StartSessionCmd command is executed with bad context")
    public void theStartSessionCmdCommandIsExecutedWithBadContext() {
        try {
            // Passing blank/null terminalId to simulate invalid context
            command = new StartSessionCmd(aggregate.id(), "teller-123", "");
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // Depending on implementation (IllegalStateException vs IllegalArgumentException), both are domain errors.
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}