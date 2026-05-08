package com.example.steps;

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
    private String sessionId = "sess-123";
    private String validTellerId = "teller-01";
    private String validTerminalId = "term-01";
    private boolean isAuthenticated = true;
    private boolean isTimeoutConfigured = true;
    private boolean isNavigationStateValid = true;

    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        isAuthenticated = true;
        isTimeoutConfigured = true;
        isNavigationStateValid = true;
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Defaults set in constructor
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Defaults set in constructor
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(sessionId);
        isAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        isTimeoutConfigured = false;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(sessionId);
        isNavigationStateValid = false;
    }

    // --- Actions ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            validTellerId,
            validTerminalId,
            isAuthenticated,
            isTimeoutConfigured,
            isNavigationStateValid
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(sessionId, event.aggregateId());
        Assertions.assertEquals(validTellerId, event.tellerId());
        Assertions.assertEquals(validTerminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // We expect IllegalStateException for business rule violations in this aggregate
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}