package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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

    // Standard defaults for a valid command
    private static final String VALID_TELLER_ID = "TELLER_01";
    private static final String VALID_TERMINAL_ID = "TERM_3270_01";
    private static final String VALID_SESSION_ID = "SESS_001";
    private static final int VALID_TIMEOUT = 30;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(VALID_SESSION_ID)
                .tellerId(VALID_TELLER_ID)
                .terminalId(VALID_TERMINAL_ID)
                .authenticated(true)
                .currentNavigationState("IDLE")
                .timeoutConfigMinutes(VALID_TIMEOUT);
        capturedException = null;
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(VALID_SESSION_ID)
                .tellerId(VALID_TELLER_ID)
                .terminalId(VALID_TERMINAL_ID)
                .authenticated(false) // VIOLATION
                .currentNavigationState("IDLE")
                .timeoutConfigMinutes(VALID_TIMEOUT);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(VALID_SESSION_ID)
                .tellerId(VALID_TELLER_ID)
                .terminalId(VALID_TERMINAL_ID)
                .authenticated(true)
                .currentNavigationState("IDLE")
                .timeoutConfigMinutes(0); // VIOLATION: Invalid config
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(VALID_SESSION_ID)
                .tellerId(VALID_TELLER_ID)
                .terminalId(VALID_TERMINAL_ID)
                .authenticated(true)
                .currentNavigationState("TRANSACTION_IN_PROGRESS") // VIOLATION: Not IDLE
                .timeoutConfigMinutes(VALID_TIMEOUT);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in setup defaults
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in setup defaults
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
        assertEquals(VALID_SESSION_ID, event.aggregateId());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
        assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // In Java, Domain Rules are often enforced via IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}