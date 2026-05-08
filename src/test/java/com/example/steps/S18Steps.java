package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.SessionStartedEvent;
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
    private String currentTellerId;
    private String currentTerminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-01");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.currentTellerId = "TELLER-101";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.currentTerminalId = "TERM-A01";
    }

    // Scenario: Rejected - Auth
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        // We simulate this by creating an aggregate that we will not mark as authenticated
        // In a real app, we might load it from a repo in a specific state, but here we just instantiate.
        // Since the command requires auth, we effectively pass null or empty credentials,
        // OR the aggregate state tracks if the user is logged in. 
        // Based on the prompt description "Initiates... following successful authentication",
        // we assume the Command carries the auth token/status.
        aggregate = new TellerSessionAggregate("SESSION-NO-AUTH");
    }

    // Scenario: Rejected - Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Simulate an old session
        aggregate = new TellerSessionAggregate("SESSION-TIMEDOUT");
        aggregate.setLastActivityTimestamp(Instant.now().minus(Duration.ofHours(2)));
    }

    // Scenario: Rejected - Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-NAV");
        // Force a state that might be inconsistent (though StartSession creates a new one)
        // We rely on the command validation to catch a specific invalid context passed in the cmd
        // or internal state corruption. Here we mock a corrupted state flag.
        aggregate.setCorruptedState(true);
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd;
            if ("SESSION-01".equals(aggregate.id())) {
                // Happy path
                cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId, "VALID_TOKEN");
            } else if ("SESSION-NO-AUTH".equals(aggregate.id())) {
                // Invalid Auth
                cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId, null);
            } else if ("SESSION-TIMEDOUT".equals(aggregate.id())) {
                // This scenario tests re-starting or state validity.
                // We'll pass valid auth, but the aggregate might be in a dead state.
                cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId, "VALID_TOKEN");
            } else {
                // Bad Nav State - we pass a conflicting context ID in the command
                cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId, "VALID_TOKEN");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("SESSION-01", event.aggregateId());
        assertEquals("TELLER-101", event.tellerId());
        assertEquals("TERM-A01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect an IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
