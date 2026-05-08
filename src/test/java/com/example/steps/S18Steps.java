package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    private String validTellerId = "TELLER_123";
    private String validTerminalId = "TERM_001";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "SESSION_" + System.currentTimeMillis();
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated(); // Ensure it's in a valid base state for success path
        this.aggregate.clearEvents();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context: The command will be constructed in the 'When' step using validTellerId
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context: The command will be constructed in the 'When' step using validTerminalId
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no error, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "SESSION_AUTH_FAIL";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // 'authenticated' defaults to false in constructor
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "SESSION_TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        // Simulate a very old activity time that implies a timeout logic would trigger
        this.aggregate.setLastActivityAt(Instant.now().minusSeconds(10000));
        this.aggregate.markActive(); // Simulate it was active before
        this.aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "SESSION_NAV_ERROR";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.markActive(); // This simulates a state conflict (already active)
        this.aggregate.clearEvents();
    }
}