package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state defaults
        aggregate.markAuthenticated(); // Assume auth is needed for valid state
        aggregate.setNavigationState("IDLE");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "TELLER-42";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "TERM-01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("TELLER-42", event.tellerId());
        assertEquals("TERM-01", event.terminalId());
        assertEquals("session-123", event.aggregateId());
    }

    // --- Scenarios for Rejections ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // Defaults to authenticated=false, so this covers the violation.
        // We ensure the other constraints are valid so we fail on auth specifically.
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Valid auth
        aggregate.setNavigationState("IDLE"); // Valid nav state
        aggregate.markAsStale(); // Invalid timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Valid auth
        // Set invalid nav state (e.g. stuck in a transaction)
        aggregate.setNavigationState("TRANSACTION_PENDING");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected a domain error exception, but none was thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
                "Expected IllegalStateException or IllegalArgumentException, got: " + thrownException.getClass().getSimpleName());
    }
}