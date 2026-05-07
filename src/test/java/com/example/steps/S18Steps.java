package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String givenTellerId;
    private String givenTerminalId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.setLastActivityToNow();
        aggregate.validateNavigationContext();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        givenTellerId = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        givenTerminalId = "terminal-05";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), givenTellerId, givenTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-01", event.tellerId());
        assertEquals("terminal-05", event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.markUnauthenticated(); // Violation
        givenTellerId = "teller-01";
        givenTerminalId = "terminal-05";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.setLastActivityToExpired(); // Violation
        givenTellerId = "teller-01";
        givenTerminalId = "terminal-05";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated();
        aggregate.invalidateNavigationContext(); // Violation
        givenTellerId = "teller-01";
        givenTerminalId = "terminal-05";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
