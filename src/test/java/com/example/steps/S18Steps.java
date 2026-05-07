package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Default setup for valid state unless overridden
        this.aggregate.markAuthenticated();
        this.aggregate.setNavigationState("IDLE");
        this.aggregate.setLastActivityAt(Instant.now()); // Recent activity
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "term-A01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "sess-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated()
        this.tellerId = "teller-unauth";
        this.terminalId = "term-B01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.setNavigationState("IDLE");
        
        // Set activity to 31 minutes ago (threshold is 30m)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
        
        this.tellerId = "teller-stale";
        this.terminalId = "term-C01";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "sess-nav-bad";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.setLastActivityAt(Instant.now());
        
        // Set a bad state (e.g., stuck in a transaction)
        this.aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");

        this.tellerId = "teller-busy";
        this.terminalId = "term-D01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals(sessionId, event.aggregateId());
        Assertions.assertEquals(tellerId, event.tellerId());
        Assertions.assertEquals(terminalId, event.terminalId());
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // We expect IllegalStateException for our domain invariant violations
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
        Assertions.assertTrue(capturedException.getMessage().length() > 0);
    }
}
