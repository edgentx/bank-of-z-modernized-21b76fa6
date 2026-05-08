package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Test Data Constants
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_B2_01";
    private static final Duration VALID_TIMEOUT = Duration.ofMinutes(30);

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        capturedException = null;
        resultEvents = null;
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION_401");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION_408");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("SESSION_NAV_ERR");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context: Field set in the When step construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context: Field set in the When step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default command construction for the happy path
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, VALID_TIMEOUT, true, true);
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        // isAuthenticated = false
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, VALID_TIMEOUT, false, true);
    }

    @When("the StartSessionCmd command is executed with excessive timeout")
    public void theStartSessionCmdCommandIsExecutedWithExcessiveTimeout() {
        // Duration > MAX (12 hours)
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, Duration.ofHours(24), true, true);
    }

    @When("the StartSessionCmd command is executed with invalid navigation context")
    public void theStartSessionCmdCommandIsExecutedWithInvalidNavigationContext() {
        // isNavigationContextValid = false
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, VALID_TIMEOUT, true, false);
    }

    private void executeCommand(String tellerId, String terminalId, Duration timeout, boolean authenticated, boolean navValid) {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                    aggregate.id(),
                    tellerId,
                    terminalId,
                    timeout,
                    authenticated,
                    navValid
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // In Java DDD, domain errors are often IllegalArgumentException or IllegalStateException
        assertTrue(
                capturedException instanceof IllegalArgumentException ||
                capturedException instanceof IllegalStateException,
                "Exception should be a domain error (IllegalArgument or IllegalState)"
        );
    }
}