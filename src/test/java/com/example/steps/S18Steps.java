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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd.StartSessionCmdBuilder cmdBuilder;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        cmdBuilder = StartSessionCmd.builder()
                .tellerId("teller-1")
                .terminalId("term-1")
                .isAuthenticated(true)
                .isActive(false) // Not started yet
                .lastActivityTimestamp(Instant.now().toEpochMilli())
                .currentContext("HOME");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-123");
        cmdBuilder = StartSessionCmd.builder()
                .tellerId("teller-1")
                .terminalId("term-1")
                .isAuthenticated(false) // Violation
                .isActive(false)
                .lastActivityTimestamp(Instant.now().toEpochMilli())
                .currentContext("HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-123");
        long expiredTime = Instant.now().minusSeconds(1000).toEpochMilli();
        cmdBuilder = StartSessionCmd.builder()
                .tellerId("teller-1")
                .terminalId("term-1")
                .isAuthenticated(true)
                .isActive(true) // Violating the active check logic
                .lastActivityTimestamp(expiredTime) // Violation: Too old
                .currentContext("HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        aggregate = new TellerSessionAggregate("session-123");
        cmdBuilder = StartSessionCmd.builder()
                .tellerId("teller-1")
                .terminalId("term-1")
                .isAuthenticated(true)
                .isActive(false)
                .lastActivityTimestamp(Instant.now().toEpochMilli())
                .currentContext(""); // Violation: Empty context
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Default is fine, but we ensure it's set
        cmdBuilder.tellerId("teller-alice");
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        cmdBuilder.terminalId("terminal-B-01");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = cmdBuilder.build();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("must") 
                || capturedException.getMessage().contains("timeout") 
                || capturedException.getMessage().contains("context"));
    }
}
