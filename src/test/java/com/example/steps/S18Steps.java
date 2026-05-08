package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to reset context before each scenario implicitly handled by Cucumber instances

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-123");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context prepared in the 'When' step via the Command
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context prepared in the 'When' step via the Command
    }

    // --- Negative Scenarios (Given violations) ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION-FAIL-AUTH");
        // Default state is !authenticated, so we leave it as is.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-FAIL-TIMEOUT");
        aggregate.markAuthenticated(); // Ensure auth passes
        aggregate.markTimedOut();       // Set the violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("SESSION-FAIL-NAV");
        aggregate.markAuthenticated(); // Ensure auth passes
        aggregate.setNavigationState("TRANSACTION_MENU"); // Set a state that is not HOME
    }

    // --- Actions ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("TELLER-01", "TERM-05");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("TELLER-01", event.tellerId());
        assertEquals("TERM-05", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // We verify it is a specific domain error (IllegalStateException is the standard for invariants in this codebase)
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
