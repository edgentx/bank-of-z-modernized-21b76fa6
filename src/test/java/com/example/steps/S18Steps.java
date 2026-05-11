package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
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
    private Exception capturedException;
    private StartSessionCmd command;

    private final String SESSION_ID = "TS-123";

    // Given steps

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup valid state: authenticated and in correct nav state
        aggregate.setAuthenticated(true);
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(Instant.now()); // Fresh
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // We create the command object here, or store params.
        // Let's assume standard params for success.
        // Command creation logic moved to 'When' for clarity or stored here.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above.
    }

    // Violation Givens

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setAuthenticated(false); // The violation
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setNavigationState("IDLE");
        // Set last activity to way back (simulating an old session context we are trying to reuse)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(3600)); // 1 hour ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS"); // The violation (expected IDLE)
    }

    // When

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // We assume valid IDs for the command payload unless the scenario implies otherwise.
        // The "Violations" are currently on the Aggregate state.
        try {
            command = new StartSessionCmd(SESSION_ID, "TELLER-01", "TERM-05");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Then

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals("TELLER-01", event.tellerId());
        assertEquals("TERM-05", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
