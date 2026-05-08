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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Test Data
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_42";
    private static final String SESSION_ID = "SESSION_123";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
        // Ensure default state is clean for success scenario
        aggregate.setNavigationState("INITIAL");
        aggregate.setIsActive(false);
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Data setup handled in execution step
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Data setup handled in execution step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand(true);
    }

    private void executeCommand(boolean isAuthenticated) {
        Command cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID, isAuthenticated);
        try {
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
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
        assertNull(thrownException, "Expected no exception to be thrown");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        assertNull(resultEvents, "Expected no events to be emitted on failure");
    }

    // Negative Scenarios Setup

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup command to run with false flag
        // We'll handle this in a specialized execution or override logic, 
        // but for simplicity, we'll just execute a non-auth command directly in 'When'
        // To keep the 'When' step generic, we assume the violation is set on the Aggregate state 
        // which forces a failure, OR we intercept the command.
        // However, the command carries the auth token. 
        // Let's modify the flow: The violation is in the Command payload for this specific step.
    }

    @When("the StartSessionCmd command is executed with invalid authentication")
    public void executeStartSessionCmdWithInvalidAuth() {
        Command cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID, false);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
        // Simulate an old last activity time (e.g., 2 hours ago)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(7200)); 
        aggregate.setIsActive(true); // Simulate it was previously active
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
        // Set a state that implies a session is in progress (e.g. CUSTOMER_VIEW)
        // and mark it active, preventing a clean StartSession
        aggregate.setIsActive(true);
        aggregate.setNavigationState("CUSTOMER_VIEW");
        aggregate.setIsAuthenticated(true); // Ensure it's not failing on auth
    }

}