package com.example.steps;

import com.example.domain.shared.Command;
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
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentTellerId;
    private String currentTerminalId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Setup pre-condition for success
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.currentTellerId = "teller-1";
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.currentTerminalId = "TERM-101"; // Matches regex in aggregate
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId);
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
        assertEquals("session.started", event.type());
        assertEquals(currentTellerId, event.tellerId());
        assertEquals(currentTerminalId, event.terminalId());
        assertNull(caughtException);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-violate-auth");
        aggregate.markUnauthenticated();
        currentTellerId = "teller-1";
        currentTerminalId = "TERM-101";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-violate-timeout");
        aggregate.markAuthenticated();
        aggregate.markActive(); // Helper to set active true
        // Set last activity to 2 hours ago (configured timeout is 30 mins)
        aggregate.setLastActivityAt(java.time.Instant.now().minus(java.time.Duration.ofHours(2)));
        currentTellerId = "teller-1";
        currentTerminalId = "TERM-101";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-violate-nav");
        aggregate.markAuthenticated();
        // Use an invalid terminal ID format to trigger context error
        currentTellerId = "teller-1";
        currentTerminalId = "INVALID-TERMINAL";
    }
}
