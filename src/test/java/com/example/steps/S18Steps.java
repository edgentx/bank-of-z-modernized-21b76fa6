package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String sessionId;
    private String tellerId;
    private String terminalId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "teller-123";
        this.terminalId = "term-A";
        // Default valid state: authenticated, no active session, correct context
        this.aggregate = new TellerSessionAggregate(this.sessionId, true, false, true);
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // tellerId initialized in "a valid TellerSession aggregate"
        Assertions.assertNotNull(this.tellerId);
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // terminalId initialized in "a valid TellerSession aggregate"
        Assertions.assertNotNull(this.terminalId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "teller-unauth";
        this.terminalId = "term-B";
        // isAuthenticated = false
        this.aggregate = new TellerSessionAggregate(this.sessionId, false, false, true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "teller-timeout";
        this.terminalId = "term-C";
        // isTimedOut = true
        this.aggregate = new TellerSessionAggregate(this.sessionId, true, true, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = UUID.randomUUID().toString();
        this.tellerId = "teller-bad-nav";
        this.terminalId = "term-D";
        // isNavStateValid = false
        this.aggregate = new TellerSessionAggregate(this.sessionId, true, false, false);
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, Instant.now());
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals(sessionId, event.aggregateId());
        Assertions.assertEquals("SESSION_STARTED", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
