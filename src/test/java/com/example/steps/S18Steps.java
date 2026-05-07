package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
        aggregate = new TellerSessionAggregate("SESSION-01");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in When step via command construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in When step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("SESSION-01", "TELLER-101", "TERM-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION-02");
        // Setup to violate auth (simulated by invalid input in this context)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-03");
        // Setup violation state handled via Command parameters (e.g. null IDs causing validation failure)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("SESSION-04");
        // Setup violation state
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
                "Expected domain error (IllegalArgument or IllegalState)");
    }

    // Context setup helpers for specific violations
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupViolationAuth() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-01");
        // Logic handled in When via modified command
    }

    // Scenario-specific overrides for when steps would be complex to map generically
    @When("the StartSessionCmd command is executed with invalid data")
    public void theStartSessionCmdCommandIsExecutedWithInvalidData() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("SESSION-ERR-01", null, "TERM-A"); // Invalid Teller ID
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Remapping scenarios to violation specific context to reduce friction in Given step
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setupViolationTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-02");
        // Trigger violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setupViolationNavState() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-03");
        // Trigger violation
    }

}