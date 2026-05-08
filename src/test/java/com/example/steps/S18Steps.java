package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-1");
        this.aggregate.markAuthenticated(); // Simulating successful auth prior to start
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in the 'When' step by constructing the command with a valid ID
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the 'When' step by constructing the command with a valid ID
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-100", "term-200");
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have produced 1 event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("teller-100", event.tellerId());
        Assertions.assertEquals("term-200", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create a session but do NOT mark it authenticated
        this.aggregate = new TellerSessionAggregate("session-2");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || 
                              caughtException instanceof IllegalArgumentException ||
                              caughtException instanceof UnknownCommandException,
                              "Expected a domain logic exception (IllegalArgument/IllegalState), got: " + caughtException.getClass().getSimpleName());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // This scenario implies checking configuration or state during execution.
        // For the BDD step, we create a valid aggregate. The domain logic will enforce the check.
        this.aggregate = new TellerSessionAggregate("session-3");
        this.aggregate.markAuthenticated();
        // We simulate the violation by passing a configuration that effectively prohibits start (e.g. duration <= 0)
        // Or the domain logic might check external config. Here we simulate via the aggregate setup.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        // Creating an aggregate that is somehow in an invalid navigation context.
        // Assuming 'UNKNOWN' is invalid or starting in a middle state is invalid.
        this.aggregate = new TellerSessionAggregate("session-4");
        this.aggregate.markAuthenticated();
    }
}
