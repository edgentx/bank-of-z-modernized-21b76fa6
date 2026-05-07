package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String currentTellerId;
    private String currentTerminalId;
    private boolean currentAuthStatus;
    private String currentNavState;
    private Instant requestTimestamp;
    
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.currentTellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.currentTerminalId = "term-01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand();
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("session.started", event.type());
        assertEquals("teller-001", event.tellerId());
        assertEquals("term-01", event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        this.currentAuthStatus = false; // Violation: not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Violation: Timestamp is too old (older than 15 mins)
        this.requestTimestamp = Instant.now().minus(Duration.ofMinutes(20));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        // Violation: Terminal is in a transaction screen, not HOME
        this.currentNavState = "TRANSACTION_DETAILS";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected a domain error exception");
        // Check for IllegalStateException or IllegalArgumentException as per domain implementation
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    // --- Helper ---

    private void executeCommand() {
        try {
            // Defaults if not set by specific Given steps
            if (currentTellerId == null) currentTellerId = "teller-001";
            if (currentTerminalId == null) currentTerminalId = "term-01";
            if (requestTimestamp == null) requestTimestamp = Instant.now();
            // Default auth true for negative tests unless explicitly set false in Given
            // However, for the 'violates auth' step, we rely on currentAuthStatus being set.
            // For the other negative tests, we assume valid auth unless specified.
            // We handle the boolean carefully: if it was never set (null/0), assume true for the context of the 'timeout/nav' tests.
            boolean auth = true; 
            // Only false if explicitly set by the 'violates auth' step
            // In a real framework we might have a context object, here we use a field check
            if (!"session-auth-fail".equals(aggregate.id())) {
                auth = true;
            } else {
                auth = currentAuthStatus;
            }

            String nav = (currentNavState != null) ? currentNavState : "HOME";

            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                currentTellerId,
                currentTerminalId,
                auth,
                nav,
                requestTimestamp
            );

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}
