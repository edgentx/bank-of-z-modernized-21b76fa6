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
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Standard Setup
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "TS-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in 'When' via construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in 'When' via construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Defaults for successful scenario
        executeCommand("T-01", "TERM-01", true);
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals("T-01", event.tellerId());
        assertEquals("TERM-01", event.terminalId());
    }

    // Failure Scenarios
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "TS-AUTH-FAIL";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set last activity to 20 minutes ago to simulate timeout (configured limit is 15)
        this.aggregate.setLastActivityAt(Instant.now().minusSeconds(20 * 60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "TS-NAV-FAIL";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set a state that is not valid for starting a session (e.g., already deep in a transaction)
        this.aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");
    }

    @When("the StartSessionCmd command is executed for violation checks")
    public void theStartSessionCmdCommandIsExecutedForViolationChecks() {
        // For these tests, we are authenticated, but the aggregate state is invalid
        // We use this for the Timeout and NavState checks
        executeCommand("T-01", "TERM-01", true);
    }

    @When("the unauthenticated StartSessionCmd command is executed")
    public void theUnauthenticatedStartSessionCmdCommandIsExecuted() {
        // Specifically for the Auth failure check
        executeCommand("T-01", "TERM-01", false);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // We expect IllegalStateException for Domain Errors in this pattern
        assertTrue(thrownException instanceof IllegalStateException);
    }

    // Helper
    private void executeCommand(String tellerId, String terminalId, boolean authenticated) {
        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, authenticated);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}
