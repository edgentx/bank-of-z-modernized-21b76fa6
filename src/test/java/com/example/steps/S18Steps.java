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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Setup Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Pre-condition: Authenticated user for success case
        aggregate.markAsAuthenticated("teller-01");
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(Instant.now()); // Active
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-999");
        // Explicitly NOT authenticated
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAsAuthenticated("teller-timeout");
        // Set last activity to 20 minutes ago (assuming 15 min timeout)
        aggregate.setLastActivityAt(Instant.now().minus(java.time.Duration.ofMinutes(20)));
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAsAuthenticated("teller-nav");
        aggregate.setLastActivityAt(Instant.now());
        // Set state to something other than IDLE to violate the rule for starting a new session
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // In this command pattern, tellerId is often tied to the auth context,
        // but the command accepts it.
        // Since we use the aggregate's internal state for validation,
        // we just ensure the command payload is valid.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Command will be constructed with this in the 'When' step
    }

    // --- Action ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Constructing a valid command for the context
            String terminal = "TERM-01";
            // Note: tellerId in command might match aggregate.tellerId, or act as input.
            // Based on the aggregate validation logic, we rely on aggregate state for auth.
            command = new StartSessionCmd("teller-01", terminal);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("TERM-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect IllegalStateException or similar domain error
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
